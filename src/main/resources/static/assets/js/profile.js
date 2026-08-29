(() => {

    const wishlist =
        document.getElementById("wishlist");

    const wishlistNotice =
        document.getElementById("wishlistNotice");


    // =========================
    // ADD WISH
    // =========================

    const addWishButton =
        document.getElementById("addWishButton");

    const addForm =
        document.getElementById("addWishlistItemForm");

    const nameInput =
        document.getElementById("wishlistItemName");

    const descriptionInput =
        document.getElementById("wishlistItemDescription");

    const addErrorElement =
        document.getElementById("wishlistError");


    // =========================
    // IMPORT
    // =========================

    const importWishlistButton =
        document.getElementById("importWishlistButton");

    const importForm =
        document.getElementById("importWishlistForm");

    const importUrlInput =
        document.getElementById("importWishlistUrl");

    const importErrorElement =
        document.getElementById("importWishlistError");

    const importSubmitButton =
        document.getElementById("importWishlistSubmit");


    // =========================
    // PROFILE
    // =========================

    const pathParts =
        window.location.pathname
            .split("/")
            .filter(Boolean);

    const isOwnProfile =
        pathParts.length === 1;

    const viewedUsername =
        isOwnProfile
            ? null
            : decodeURIComponent(pathParts[1]);


    if (isOwnProfile) {
        addWishButton?.classList.remove("d-none");
        importWishlistButton?.classList.remove("d-none");
    }


    // =========================
    // NOTICE
    // =========================

    function showNotice(message, type = "success") {

        if (!wishlistNotice) {
            return;
        }

        wishlistNotice.replaceChildren();

        if (!message) {
            return;
        }

        const alert =
            document.createElement("div");

        alert.classList.add(
            "alert",
            `alert-${type}`,
            "mb-0"
        );

        alert.setAttribute(
            "role",
            "alert"
        );

        alert.textContent =
            message;

        wishlistNotice.appendChild(alert);
    }


    // =========================
    // LOAD WISHLIST
    // =========================

    async function loadWishlist() {

        if (!wishlist) {
            return;
        }

        try {

            const url =
                isOwnProfile
                    ? "/api/v1/users/me/wishlist"
                    : `/api/v1/users/${encodeURIComponent(viewedUsername)}/wishlist`;

            const items =
                await api.request(url);

            renderWishlist(items);

        } catch (error) {

            console.error(error);

            wishlist.textContent =
                "Не удалось загрузить wishlist";
        }
    }


    // =========================
    // RENDER
    // =========================

    function renderWishlist(items) {

        wishlist.replaceChildren();


        if (items.length === 0) {

            const empty =
                document.createElement("p");

            empty.classList.add(
                "text-muted"
            );

            empty.textContent =
                "Wishlist пока пуст";

            wishlist.appendChild(empty);

            return;
        }


        for (const item of items) {

            const card =
                document.createElement("div");

            card.classList.add(
                "card",
                "mb-3"
            );


            const body =
                document.createElement("div");

            body.classList.add(
                "card-body"
            );


            const title =
                document.createElement("h5");

            title.classList.add(
                "card-title"
            );

            title.textContent =
                item.name;

            body.appendChild(title);


            if (item.description) {

                const description =
                    document.createElement("p");

                description.classList.add(
                    "card-text"
                );

                description.textContent =
                    item.description;

                body.appendChild(description);
            }


            if (isOwnProfile) {

                const deleteButton =
                    document.createElement("button");

                deleteButton.classList.add(
                    "btn",
                    "btn-outline-danger"
                );

                deleteButton.type =
                    "button";

                deleteButton.textContent =
                    "Удалить";

                deleteButton.addEventListener(
                    "click",
                    () => deleteItem(item.id)
                );

                body.appendChild(
                    deleteButton
                );
            }


            card.appendChild(body);

            wishlist.appendChild(card);
        }
    }


    // =========================
    // DELETE
    // =========================

    async function deleteItem(itemId) {

        if (!isOwnProfile) {
            return;
        }

        try {

            await api.request(
                `/api/v1/users/me/wishlist/${itemId}`,
                {
                    method: "DELETE"
                }
            );

            showNotice("");

            await loadWishlist();

        } catch (error) {

            console.error(error);

            showNotice(
                error.message,
                "danger"
            );
        }
    }


    // =========================
    // ADD WISH
    // =========================

    if (addForm && isOwnProfile) {

        addForm.addEventListener(
            "submit",
            async (event) => {

                event.preventDefault();


                if (addErrorElement) {
                    addErrorElement.textContent = "";
                }


                try {

                    await api.request(
                        "/api/v1/users/me/wishlist",
                        {
                            method: "POST",

                            headers: {
                                "Content-Type":
                                    "application/json"
                            },

                            body: JSON.stringify({
                                name:
                                    nameInput.value.trim(),

                                description:
                                    descriptionInput.value.trim()
                            })
                        }
                    );


                    addForm.reset();


                    const modalElement =
                        document.getElementById(
                            "addWishlistItemModal"
                        );

                    const modal =
                        bootstrap.Modal.getInstance(
                            modalElement
                        );

                    modal?.hide();


                    showNotice(
                        "Wish added."
                    );


                    await loadWishlist();

                } catch (error) {

                    console.error(error);

                    if (addErrorElement) {
                        addErrorElement.textContent =
                            error.message;
                    }
                }
            }
        );
    }


    // =========================
    // IMPORT WISHLIST
    // =========================

    if (importForm && isOwnProfile) {

        importForm.addEventListener(
            "submit",
            async (event) => {

                event.preventDefault();


                if (importErrorElement) {
                    importErrorElement.textContent = "";
                }


                const url =
                    importUrlInput.value.trim();


                if (!url) {

                    if (importErrorElement) {
                        importErrorElement.textContent =
                            "Укажи ссылку на wishlist";
                    }

                    return;
                }


                const previousText =
                    importSubmitButton?.textContent;


                if (importSubmitButton) {

                    importSubmitButton.disabled =
                        true;

                    importSubmitButton.textContent =
                        "Importing...";
                }


                try {

                    const importedItems =
                        await api.request(
                            "/api/v1/users/me/wishlist/import",
                            {
                                method: "POST",

                                headers: {
                                    "Content-Type":
                                        "application/json"
                                },

                                body: JSON.stringify({
                                    url
                                })
                            }
                        );


                    importForm.reset();


                    const modalElement =
                        document.getElementById(
                            "importWishlistModal"
                        );

                    const modal =
                        bootstrap.Modal.getInstance(
                            modalElement
                        );

                    modal?.hide();


                    const importedCount =
                        Array.isArray(importedItems)
                            ? importedItems.length
                            : 0;


                    showNotice(
                        `Импорт завершён. Добавлено: ${importedCount}`
                    );


                    await loadWishlist();

                } catch (error) {

                    console.error(error);

                    if (importErrorElement) {
                        importErrorElement.textContent =
                            error.message;
                    }

                } finally {

                    if (importSubmitButton) {

                        importSubmitButton.disabled =
                            false;

                        importSubmitButton.textContent =
                            previousText || "Import";
                    }
                }
            }
        );
    }


    loadWishlist();

})();