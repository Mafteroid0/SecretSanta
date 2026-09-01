(() => {

    const roomName =
        document.getElementById("roomName");

    const roomDeadline =
        document.getElementById("roomDeadline");

    const roomStatus =
        document.getElementById("roomStatus");

    const roomError =
        document.getElementById("roomError");

    const participantsList =
        document.getElementById("participantsList");

    const startGameButton =
        document.getElementById("startGameButton");

    const leaveGameButton =
        document.getElementById("leaveGameButton");

    const assignmentCard =
        document.getElementById("assignmentCard");

    const giftedUserName =
        document.getElementById("giftedUserName");

    const giftedUserLink =
        document.getElementById("giftedUserLink");

    const waitingForStart =
        document.getElementById("waitingForStart");


    const pathParts =
        window.location.pathname
            .split("/")
            .filter(Boolean);

    const eventId =
        pathParts[pathParts.length - 1];


    async function loadRoom() {
        try {

            const [event, currentUser, participants] =
                await Promise.all([

                    api.request(
                        `/api/v1/events/${encodeURIComponent(eventId)}`
                    ),

                    api.request(
                        "/api/v1/users/me"
                    ),

                    api.request(
                        `/api/v1/events/${encodeURIComponent(eventId)}/participants`
                    )
                ]);

            renderEvent(event);
            renderParticipants(participants);
            renderStartButton(event, currentUser);

            if (event.started) {
                await loadAssignment();
            } else {
                showWaitingState();
            }

        } catch (error) {

            console.error(
                "Failed to load room:",
                error
            );

            if (roomError) {
                roomError.textContent =
                    error.message ?? "Не удалось загрузить игру";
            }
        }
    }


    function renderEvent(event) {

        if (roomName) {
            roomName.textContent = event.name;
        }

        if (roomDeadline) {
            roomDeadline.textContent =
                new Date(event.deadline)
                    .toLocaleString();
        }

        if (roomStatus) {
            roomStatus.textContent =
                event.started
                    ? "Игра началась"
                    : "Игра ещё не началась";
        }
    }


    function renderParticipants(participants) {

        if (!participantsList) {
            return;
        }

        participantsList.replaceChildren();


        if (participants.length === 0) {

            const empty =
                document.createElement("p");

            empty.textContent =
                "Участников пока нет";

            participantsList.appendChild(empty);

            return;
        }


        for (const participant of participants) {

            const container =
                document.createElement("div");

            container.classList.add(
                "d-flex",
                "align-items-center",
                "gap-2",
                "mb-2"
            );


            const userLink =
                document.createElement("a");

            userLink.href =
                `/profile/${encodeURIComponent(
                    participant.username
                )}`;

            userLink.textContent =
                participant.displayName;


            container.appendChild(userLink);


            if (participant.owner) {

                const ownerBadge =
                    document.createElement("span");

                ownerBadge.classList.add(
                    "badge",
                    "text-bg-primary"
                );

                ownerBadge.textContent = "Owner";

                container.appendChild(ownerBadge);
            }


            participantsList.appendChild(container);
        }
    }


    function renderStartButton(event, currentUser) {

        if (!startGameButton) {
            return;
        }

        const isOwner =
            event.ownerId === currentUser.id;

        if (isOwner && !event.started) {

            startGameButton.classList.remove(
                "d-none"
            );

        } else {

            startGameButton.classList.add(
                "d-none"
            );
        }
    }


    function showWaitingState() {

        if (assignmentCard) {
            assignmentCard.classList.add(
                "d-none"
            );
        }

        if (waitingForStart) {
            waitingForStart.classList.remove(
                "d-none"
            );
        }
    }


    async function loadAssignment() {

        const assignment =
            await api.request(
                `/api/v1/events/${encodeURIComponent(eventId)}/participants/me/assignment`
            );


        if (waitingForStart) {
            waitingForStart.classList.add(
                "d-none"
            );
        }


        if (giftedUserName) {
            giftedUserName.textContent =
                assignment.displayName;
        }


        if (giftedUserLink) {

            giftedUserLink.href =
                `/profile/${encodeURIComponent(
                    assignment.username
                )}`;
        }


        if (assignmentCard) {
            assignmentCard.classList.remove(
                "d-none"
            );
        }
    }


    if (startGameButton) {

        startGameButton.addEventListener(
            "click",
            async () => {

                startGameButton.disabled = true;

                try {

                    await api.request(
                        `/api/v1/events/${encodeURIComponent(eventId)}/start`,
                        {
                            method: "POST"
                        }
                    );

                    startGameButton.classList.add(
                        "d-none"
                    );

                    await loadAssignment();

                } catch (error) {

                    console.error(error);

                    if (roomError) {
                        roomError.textContent =
                            error.message;
                    }

                    startGameButton.disabled = false;
                }
            }
        );
    }


    if (leaveGameButton) {

        leaveGameButton.addEventListener(
            "click",
            async () => {

                const confirmed = window.confirm(
                    "Вы уверены, что хотите выйти из игры?"
                );

                if (!confirmed) {
                    return;
                }

                leaveGameButton.disabled = true;

                if (roomError) {
                    roomError.textContent = "";
                }

                try {

                    await api.request(
                        `/api/v1/events/${encodeURIComponent(eventId)}/participants/me`,
                        {
                            method: "DELETE"
                        }
                    );

                    window.location.href = "/games";

                } catch (error) {

                    console.error(error);

                    if (roomError) {
                        roomError.textContent =
                            error.message ?? "Не удалось выйти из игры";
                    }

                    leaveGameButton.disabled = false;
                }
            }
        );
    }


    loadRoom();

})();