(() => {

    const gameContent =
        document.getElementById(
            "joinGameContent"
        );

    const gameName =
        document.getElementById(
            "joinGameName"
        );

    const gameDeadline =
        document.getElementById(
            "joinGameDeadline"
        );

    const joinButton =
        document.getElementById(
            "joinGameButton"
        );

    const loading =
        document.getElementById(
            "joinGameLoading"
        );

    const errorElement =
        document.getElementById(
            "joinGameError"
        );


    const pathParts =
        window.location.pathname
            .split("/")
            .filter(Boolean);


    const eventId =
        pathParts[pathParts.length - 1];


    function showError(message) {

        if (!errorElement) {
            return;
        }

        errorElement.textContent =
            message;

        errorElement.classList.remove(
            "d-none"
        );
    }


    function hideError() {

        if (!errorElement) {
            return;
        }

        errorElement.textContent =
            "";

        errorElement.classList.add(
            "d-none"
        );
    }


    function formatDeadline(deadline) {

        return new Intl.DateTimeFormat(
            "ru-RU",
            {
                day: "2-digit",
                month: "2-digit",
                year: "numeric"
            }
        ).format(
            new Date(deadline)
        );
    }


    async function loadGame() {

        try {

            const event =
                await api.request(
                    `/api/v1/events/${encodeURIComponent(eventId)}`
                );


            if (gameName) {

                gameName.textContent =
                    event.name;
            }


            if (gameDeadline) {

                gameDeadline.textContent =
                    formatDeadline(
                        event.deadline
                    );
            }


            loading?.classList.add(
                "d-none"
            );


            gameContent?.classList.remove(
                "d-none"
            );


        } catch (error) {

            console.error(
                "Failed to load game:",
                error
            );


            loading?.classList.add(
                "d-none"
            );


            if (error.status === 404) {

                showError(
                    "Game not found"
                );

            } else {

                showError(
                    error.message ??
                    "Failed to load game"
                );
            }
        }
    }


    if (joinButton) {

        joinButton.addEventListener(
            "click",
            async () => {

                joinButton.disabled =
                    true;

                hideError();


                try {

                    await api.request(
                        `/api/v1/events/${encodeURIComponent(eventId)}/join`,
                        {
                            method: "POST"
                        }
                    );


                    window.location.href =
                        `/room/${encodeURIComponent(eventId)}`;


                } catch (error) {

                    console.error(
                        "Failed to join game:",
                        error
                    );


                    if (error.status === 409) {

                        showError(
                            "You are already participating in this game"
                        );

                    } else if (error.status === 404) {

                        showError(
                            "Game not found"
                        );

                    } else {

                        showError(
                            error.message ??
                            "Failed to join game"
                        );
                    }


                    joinButton.disabled =
                        false;
                }
            }
        );
    }


    loadGame();

})();