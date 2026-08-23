(() => {
    const wishlist = document.getElementById("wishlist");
    const addWishButton = document.getElementById("addWishButton");
    const form = document.getElementById("addWishlistItemForm");
    const nameInput = document.getElementById("wishlistItemName");
    const descriptionInput = document.getElementById("wishlistItemDescription");
    const errorElement = document.getElementById("wishlistError");

    const pathParts = window.location.pathname
        .split("/")
        .filter(Boolean);

    const isOwnProfile = pathParts.length === 1;

    const viewedUsername = isOwnProfile
        ? null
        : decodeURIComponent(pathParts[1]);

    if (isOwnProfile) {
        addWishButton?.classList.remove("d-none");
    }


    async function loadWishlist() {
        if (!wishlist) {
            return;
        }

        try {
            const url = isOwnProfile
                ? "/api/v1/users/me/wishlist"
                : `/api/v1/users/${encodeURIComponent(viewedUsername)}/wishlist`;

            const items = await api.request(url);

            renderWishlist(items);
        } catch (error) {
            console.error(error);

            wishlist.textContent =
                "Не удалось загрузить wishlist";
        }
    }


    function renderWishlist(items) {
        wishlist.replaceChildren();

        if (items.length === 0) {
            const empty = document.createElement("p");

            empty.classList.add("text-muted");
            empty.textContent = "Wishlist пока пуст";

            wishlist.appendChild(empty);

            return;
        }

        for (const item of items) {
            const card = document.createElement("div");

            card.classList.add(
                "card",
                "mb-3"
            );

            const body = document.createElement("div");

            body.classList.add("card-body");

            const title = document.createElement("h5");

            title.classList.add("card-title");
            title.textContent = item.name;

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

                deleteButton.type = "button";
                deleteButton.textContent = "Удалить";

                deleteButton.addEventListener(
                    "click",
                    () => deleteItem(item.id)
                );

                body.appendChild(deleteButton);
            }

            card.appendChild(body);

            wishlist.appendChild(card);
        }
    }


    async function deleteItem(itemId) {
        try {
            await api.request(
                `/api/v1/users/me/wishlist/${itemId}`,
                {
                    method: "DELETE"
                }
            );

            await loadWishlist();
        } catch (error) {
            console.error(error);
        }
    }


    if (form && isOwnProfile) {
        form.addEventListener(
            "submit",
            async (event) => {

                event.preventDefault();

                if (errorElement) {
                    errorElement.textContent = "";
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

                    form.reset();

                    const modalElement =
                        document.getElementById(
                            "addWishlistItemModal"
                        );

                    const modal =
                        bootstrap.Modal.getInstance(
                            modalElement
                        );

                    modal?.hide();

                    await loadWishlist();

                } catch (error) {
                    console.error(error);

                    if (errorElement) {
                        errorElement.textContent =
                            error.message;
                    }
                }
            }
        );
    }


    loadWishlist();
})();