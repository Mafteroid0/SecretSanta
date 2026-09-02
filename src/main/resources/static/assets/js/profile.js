(() => {
    const wishlist = document.getElementById("wishlist");
    const notice = document.getElementById("wishlistNotice");
    const addButton = document.getElementById("addWishButton");
    const addForm = document.getElementById("addWishlistItemForm");
    const nameInput = document.getElementById("wishlistItemName");
    const descriptionInput = document.getElementById("wishlistItemDescription");
    const imageInput = document.getElementById("wishlistItemImage");
    const addError = document.getElementById("wishlistError");
    const importButton = document.getElementById("importWishlistButton");
    const importForm = document.getElementById("importWishlistForm");
    const importInput = document.getElementById("importWishlistUrl");
    const importError = document.getElementById("importWishlistError");
    const importSubmit = document.getElementById("importWishlistSubmit");
    const editForm = document.getElementById("profileEditForm");
    const displayNameInput = editForm?.querySelector('[name="displayName"]');
    const editError = document.getElementById("profileEditError");
    const profileDisplayName = document.getElementById("profileDisplayName");
    const profileAvatar = document.getElementById("profileAvatar");
    const avatarInput = document.getElementById("avatarInput");
    const changeAvatarButton = document.getElementById("changeAvatarButton");

    const defaultAvatar = "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 100 100'%3E%3Crect width='100' height='100' rx='50' fill='%23343a40'/%3E%3Ccircle cx='50' cy='36' r='18' fill='%23adb5bd'/%3E%3Cpath d='M18 92c2-24 16-38 32-38s30 14 32 38' fill='%23adb5bd'/%3E%3C/svg%3E";

    const pathParts = window.location.pathname.split("/").filter(Boolean);
    const isOwnProfile = pathParts.length === 1;
    const viewedUsername = isOwnProfile ? null : decodeURIComponent(pathParts[1]);

    function loadAvatar() {
        const userId = profileAvatar?.dataset.userId;
        if (!profileAvatar || !userId) return;

        profileAvatar.onerror = () => {
            profileAvatar.onerror = null;
            profileAvatar.src = defaultAvatar;
        };

        profileAvatar.src = `/api/v1/users/${encodeURIComponent(userId)}/avatar`;
    }

    if (isOwnProfile) {
        editForm?.classList.remove("hidden");
        addButton?.classList.remove("hidden");
        importButton?.classList.remove("hidden");
        changeAvatarButton?.classList.remove("hidden");
    }

    function showNotice(message, type = "success") {
        notice.replaceChildren();
        if (!message) return;

        const element = document.createElement("div");
        element.className = `alert alert-${type}`;
        element.setAttribute("role", "alert");
        element.textContent = message;
        notice.append(element);
    }

    function renderWishlist(items) {
        wishlist.replaceChildren();

        if (!items.length) {
            wishlist.textContent = "Вишлист пока пуст.";
            wishlist.classList.add("muted");
            return;
        }

        wishlist.classList.remove("muted");

        for (const item of items) {
            const card = document.createElement("article");
            const image = document.createElement("img");
            const title = document.createElement("h3");

            card.className = "wish-card";

            image.src = `/api/v1/wishlist/${encodeURIComponent(item.id)}/image`;
            image.alt = item.name;
            image.style.cssText = "width:100%;max-height:260px;object-fit:cover;border-radius:12px;margin-bottom:14px;";
            image.onerror = () => image.remove();

            title.textContent = item.name;

            card.append(image, title);

            if (item.description) {
                const description = document.createElement("p");
                description.textContent = item.description;
                card.append(description);
            }

            if (isOwnProfile) {
                const deleteButton = document.createElement("button");
                deleteButton.className = "btn btn-danger-soft";
                deleteButton.type = "button";
                deleteButton.textContent = "Удалить";
                deleteButton.addEventListener("click", () => deleteItem(item.id));
                card.append(deleteButton);
            }

            wishlist.append(card);
        }
    }

    async function loadWishlist() {
        try {
            const url = isOwnProfile
                ? "/api/v1/users/me/wishlist"
                : `/api/v1/users/${encodeURIComponent(viewedUsername)}/wishlist`;

            renderWishlist(await api.request(url));
        } catch {
            wishlist.textContent = "Не удалось загрузить вишлист.";
            wishlist.classList.add("error");
        }
    }

    async function deleteItem(itemId) {
        if (!isOwnProfile) return;

        try {
            await api.request(`/api/v1/users/me/wishlist/${itemId}`, {
                method: "DELETE"
            });

            showNotice("");
            await loadWishlist();
        } catch (error) {
            showNotice(error.message, "danger");
        }
    }

    document.querySelectorAll("[data-dialog-open]").forEach(button => {
        button.addEventListener("click", () =>
            document.getElementById(button.dataset.dialogOpen)?.showModal()
        );
    });

    document.querySelectorAll("[data-dialog-close]").forEach(button => {
        button.addEventListener("click", () =>
            button.closest("dialog")?.close()
        );
    });

    changeAvatarButton?.addEventListener("click", () => avatarInput?.click());

    avatarInput?.addEventListener("change", async () => {
        const file = avatarInput.files?.[0];
        if (!file) return;

        const formData = new FormData();
        formData.append("file", file);

        changeAvatarButton.disabled = true;

        try {
            await api.request("/api/v1/users/me/avatar", {
                method: "POST",
                body: formData
            });

            const userId = profileAvatar.dataset.userId;
            profileAvatar.onerror = () => {
                profileAvatar.onerror = null;
                profileAvatar.src = defaultAvatar;
            };
            profileAvatar.src = `/api/v1/users/${encodeURIComponent(userId)}/avatar?v=${Date.now()}`;
        } catch (error) {
            showNotice(error.message, "danger");
        } finally {
            avatarInput.value = "";
            changeAvatarButton.disabled = false;
        }
    });

    editForm?.addEventListener("submit", async event => {
        event.preventDefault();
        if (!isOwnProfile) return;

        const displayName = displayNameInput?.value.trim();
        if (editError) editError.textContent = "";

        if (!displayName) {
            if (editError) editError.textContent = "Введите отображаемое имя.";
            return;
        }

        try {
            const user = await api.request("/api/v1/users/me", {
                method: "PATCH",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({displayName})
            });

            if (profileDisplayName) profileDisplayName.textContent = user.displayName;
            if (displayNameInput) displayNameInput.value = user.displayName;
        } catch (error) {
            if (editError) editError.textContent = error.message;
            else showNotice(error.message, "danger");
        }
    });

    addForm?.addEventListener("submit", async event => {
        event.preventDefault();
        addError.textContent = "";

        try {
            const item = await api.request("/api/v1/users/me/wishlist", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({
                    name: nameInput.value.trim(),
                    description: descriptionInput.value.trim()
                })
            });

            const file = imageInput.files?.[0];

            if (file) {
                const formData = new FormData();
                formData.append("file", file);

                await api.request(`/api/v1/users/me/wishlist/${item.id}/image`, {
                    method: "POST",
                    body: formData
                });
            }

            addForm.reset();
            document.getElementById("addWishlistItemModal").close();
            await loadWishlist();
        } catch (error) {
            addError.textContent = error.message;
        }
    });

    importForm?.addEventListener("submit", async event => {
        event.preventDefault();

        const url = importInput.value.trim();
        importError.textContent = "";

        if (!url) {
            importError.textContent = "Укажите ссылку на вишлист.";
            return;
        }

        const previousText = importSubmit.textContent;
        importSubmit.disabled = true;
        importSubmit.textContent = "Импортируем…";

        try {
            const importedItems = await api.request(
                "/api/v1/users/me/wishlist/import",
                {
                    method: "POST",
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify({url})
                }
            );

            importForm.reset();
            document.getElementById("importWishlistModal").close();
            showNotice(
                `Импорт завершён. Добавлено: ${
                    Array.isArray(importedItems) ? importedItems.length : 0
                }`
            );

            await loadWishlist();
        } catch (error) {
            importError.textContent = error.message;
        } finally {
            importSubmit.disabled = false;
            importSubmit.textContent = previousText;
        }
    });

    loadAvatar();
    loadWishlist();
})();