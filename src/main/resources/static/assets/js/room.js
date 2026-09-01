(() => {

    const roomName =
        document.getElementById(
            "roomName"
        );

    const roomDeadline =
        document.getElementById(
            "roomDeadline"
        );

    const roomStatus =
        document.getElementById(
            "roomStatus"
        );

    const roomError =
        document.getElementById(
            "roomError"
        );

    const participantsList =
        document.getElementById(
            "participantsList"
        );

    const startGameButton =
        document.getElementById(
            "startGameButton"
        );

    const gameActionButton =
        document.getElementById(
            "gameActionButton"
        );

    const copyInviteButton =
        document.getElementById(
            "copyInviteButton"
        );

    const assignmentCard =
        document.getElementById(
            "assignmentCard"
        );

    const giftedUserName =
        document.getElementById(
            "giftedUserName"
        );

    const giftedUserLink =
        document.getElementById(
            "giftedUserLink"
        );

    const waitingForStart =
        document.getElementById(
            "waitingForStart"
        );


    const pathParts =
        window.location.pathname
            .split("/")
            .filter(Boolean);


    const eventId =
        pathParts[pathParts.length - 1];


    let currentEvent = null;

    let currentUser = null;


    async function loadRoom() {

        try {

            const [event, user, participants] =
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


            currentEvent =
                event;

            currentUser =
                user;


            renderEvent(
                event
            );


            renderParticipants(
                participants
            );


            renderControls(
                event,
                user
            );


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
                    error.message ??
                    "Не удалось загрузить игру";
            }
        }
    }


    function renderEvent(event) {

        if (roomName) {

            roomName.textContent =
                event.name;
        }


        if (roomDeadline) {

            roomDeadline.textContent =
                new Date(
                    event.deadline
                ).toLocaleString();
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
                document.createElement(
                    "p"
                );


            empty.textContent =
                "Участников пока нет";


            participantsList.appendChild(
                empty
            );


            return;
        }


        for (const participant of participants) {

            const container =
                document.createElement(
                    "div"
                );


            container.classList.add(
                "d-flex",
                "align-items-center",
                "gap-2",
                "mb-2"
            );


            const userLink =
                document.createElement(
                    "a"
                );


            userLink.href =
                `/profile/${encodeURIComponent(
                    participant.username
                )}`;


            userLink.textContent =
                participant.displayName;


            container.appendChild(
                userLink
            );


            if (participant.owner) {

                const ownerBadge =
                    document.createElement(
                        "span"
                    );


                ownerBadge.classList.add(
                    "badge",
                    "text-bg-primary"
                );


                ownerBadge.textContent =
                    "Owner";


                container.appendChild(
                    ownerBadge
                );
            }


            participantsList.appendChild(
                container
            );
        }
    }


    function renderControls(
        event,
        user
    ) {

        const isOwner =
            event.ownerId === user.id;
        if (startGameButton) {

            if (
                isOwner &&
                !event.started
            ) {

                startGameButton
                    .classList
                    .remove(
                        "d-none"
                    );

            } else {

                startGameButton
                    .classList
                    .add(
                        "d-none"
                    );
            }
        }

        if (!gameActionButton) {
            return;
        }


        gameActionButton
            .classList
            .remove(
                "d-none"
            );


        if (isOwner) {

            gameActionButton.textContent =
                "Delete game";

            gameActionButton.dataset.action =
                "delete";

        } else {

            gameActionButton.textContent =
                "Leave game";

            gameActionButton.dataset.action =
                "leave";
        }
    }


    function showWaitingState() {

        if (assignmentCard) {

            assignmentCard
                .classList
                .add(
                    "d-none"
                );
        }


        if (waitingForStart) {

            waitingForStart
                .classList
                .remove(
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

            waitingForStart
                .classList
                .add(
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

            assignmentCard
                .classList
                .remove(
                    "d-none"
                );
        }
    }

    if (copyInviteButton) {

        copyInviteButton.addEventListener(
            "click",
            async () => {

                const inviteLink =
                    `${window.location.origin}/join/${encodeURIComponent(eventId)}`;


                try {

                    await navigator.clipboard.writeText(
                        inviteLink
                    );


                    const oldText =
                        copyInviteButton.textContent;


                    copyInviteButton.textContent =
                        "Copied!";


                    setTimeout(
                        () => {

                            copyInviteButton.textContent =
                                oldText;

                        },
                        1500
                    );


                } catch (error) {

                    console.error(
                        "Failed to copy invite link:",
                        error
                    );


                    if (roomError) {

                        roomError.textContent =
                            "Не удалось скопировать ссылку";
                    }
                }
            }
        );
    }

    if (startGameButton) {

        startGameButton.addEventListener(
            "click",
            async () => {

                startGameButton.disabled =
                    true;


                if (roomError) {

                    roomError.textContent =
                        "";
                }


                try {

                    const event =
                        await api.request(
                            `/api/v1/events/${encodeURIComponent(eventId)}/start`,
                            {
                                method: "POST"
                            }
                        );


                    currentEvent =
                        event;


                    renderEvent(
                        currentEvent
                    );


                    renderControls(
                        currentEvent,
                        currentUser
                    );


                    await loadAssignment();


                } catch (error) {

                    console.error(
                        error
                    );


                    if (roomError) {

                        roomError.textContent =
                            error.message ??
                            "Не удалось начать игру";
                    }


                    startGameButton.disabled =
                        false;
                }
            }
        );
    }

    if (gameActionButton) {

        gameActionButton.addEventListener(
            "click",
            async () => {

                const action =
                    gameActionButton.dataset.action;


                if (!action) {
                    return;
                }


                const confirmationMessage =
                    action === "delete"
                        ? "Вы уверены, что хотите удалить игру?"
                        : "Вы уверены, что хотите выйти из игры?";


                const confirmed =
                    window.confirm(
                        confirmationMessage
                    );


                if (!confirmed) {
                    return;
                }


                gameActionButton.disabled =
                    true;


                if (roomError) {

                    roomError.textContent =
                        "";
                }


                try {

                    if (action === "leave") {

                        await api.request(
                            `/api/v1/events/${encodeURIComponent(eventId)}/participants/me`,
                            {
                                method: "DELETE"
                            }
                        );

                    } else if (action === "delete") {

                        await api.request(
                            `/api/v1/events/${encodeURIComponent(eventId)}`,
                            {
                                method: "DELETE"
                            }
                        );
                    }


                    window.location.href =
                        "/games";


                } catch (error) {

                    console.error(
                        error
                    );


                    if (roomError) {

                        roomError.textContent =
                            error.message ??
                            "Не удалось выполнить действие";
                    }


                    gameActionButton.disabled =
                        false;
                }
            }
        );
    }


    loadRoom();

})();