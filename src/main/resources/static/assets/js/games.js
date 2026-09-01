(() => {

    const gamesList =
        document.getElementById("gamesList");

    const joinForm =
        document.getElementById("joinEventForm");

    const joinInput =
        document.getElementById("joinEventId");

    const joinError =
        document.getElementById("joinGameError");


    const UUID_PATTERN =
        /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;


    function extractEventId(value) {

        const input =
            value.trim();

        if (UUID_PATTERN.test(input)) {
            return input;
        }

        const match =
            input.match(
                /(?:^|\/)join\/([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})(?:\/|[?#]|$)/i
            );


        if (match) {
            return match[1];
        }


        return null;
    }


    async function loadEvents() {

        if (!gamesList) {
            return;
        }

        try {

            const events =
                await api.request(
                    "/api/v1/events"
                );

            renderEvents(events);

        } catch (error) {

            console.error(
                "Failed to load events:",
                error
            );

            gamesList.textContent =
                "Не удалось загрузить игры";
        }
    }


    function renderEvents(events) {

        gamesList.replaceChildren();


        if (events.length === 0) {

            const empty =
                document.createElement("p");

            empty.textContent =
                "Вы пока не участвуете ни в одной игре";

            gamesList.appendChild(empty);

            return;
        }


        for (const event of events) {

            const container =
                document.createElement("div");

            const link =
                document.createElement("a");

            link.href =
                `/room/${event.id}`;

            link.textContent =
                event.name;


            const deadline =
                document.createElement("div");

            deadline.textContent =
                new Date(event.deadline)
                    .toLocaleString();


            container.appendChild(link);

            container.appendChild(deadline);

            gamesList.appendChild(container);
        }
    }


    if (joinForm) {

        joinForm.addEventListener(
            "submit",
            async (event) => {

                event.preventDefault();


                const eventId =
                    extractEventId(
                        joinInput.value
                    );


                joinInput.classList.remove(
                    "is-invalid"
                );


                if (joinError) {
                    joinError.textContent = "";
                }

                if (!eventId) {

                    joinInput.classList.add(
                        "is-invalid"
                    );

                    if (joinError) {

                        joinError.textContent =
                            "Введите корректный ID игры или ссылку-приглашение";
                    }

                    return;
                }


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

                    console.error(error);


                    joinInput.classList.add(
                        "is-invalid"
                    );


                    if (!joinError) {
                        return;
                    }


                    switch (error.status) {

                        case 400:

                            joinError.textContent =
                                "Некорректный ID игры";

                            break;


                        case 404:

                            joinError.textContent =
                                "Игра с таким ID не найдена";

                            break;


                        case 409:

                            joinError.textContent =
                                error.message ??
                                "Вы не можете присоединиться к этой игре";

                            break;


                        default:

                            joinError.textContent =
                                "Не удалось присоединиться к игре";
                    }
                }
            }
        );
    }


    loadEvents();

})();