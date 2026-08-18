(() => {

    const roomName =
        document.getElementById("roomName");

    const roomDeadline =
        document.getElementById("roomDeadline");

    const roomStatus =
        document.getElementById("roomStatus");

    const participantsList =
        document.getElementById("participantsList");

    const roomError =
        document.getElementById("roomError");


    const pathParts =
        window.location.pathname
            .split("/")
            .filter(Boolean);

    const eventId =
        pathParts[pathParts.length - 1];


    async function loadRoom() {

        try {

            const [event, participants] =
                await Promise.all([
                    api.request(
                        `/api/v1/events/${encodeURIComponent(eventId)}`
                    ),

                    api.request(
                        `/api/v1/events/${encodeURIComponent(eventId)}/participants`
                    )
                ]);

            renderEvent(event);
            renderParticipants(participants);

        } catch (error) {

            console.error(
                "Failed to load room:",
                error
            );

            if (roomError) {
                roomError.textContent =
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

            const participantElement =
                document.createElement("div");

            participantElement.classList.add(
                "participant"
            );


            const name =
                document.createElement("a");

            name.href =
                `/profile/${encodeURIComponent(
                    participant.username
                )}`;

            name.textContent =
                participant.displayName;


            participantElement.appendChild(name);


            if (participant.owner) {

                const ownerBadge =
                    document.createElement("span");

                ownerBadge.textContent =
                    " Owner";

                ownerBadge.classList.add(
                    "badge",
                    "text-bg-primary",
                    "ms-2"
                );

                participantElement.appendChild(
                    ownerBadge
                );
            }


            participantsList.appendChild(
                participantElement
            );
        }
    }


    loadRoom();

})();