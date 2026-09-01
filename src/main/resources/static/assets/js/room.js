(() => {
    const roomName = document.getElementById("roomName");
    const roomDeadline = document.getElementById("roomDeadline");
    const roomStatus = document.getElementById("roomStatus");
    const roomError = document.getElementById("roomError");
    const participantsList = document.getElementById("participantsList");
    const startButton = document.getElementById("startGameButton");
    const actionButton = document.getElementById("gameActionButton");
    const copyButton = document.getElementById("copyInviteButton");
    const assignmentCard = document.getElementById("assignmentCard");
    const giftedUserName = document.getElementById("giftedUserName");
    const giftedUserLink = document.getElementById("giftedUserLink");
    const waiting = document.getElementById("waitingForStart");
    const eventId = window.location.pathname.split("/").filter(Boolean).at(-1);
    let currentEvent;
    let currentUser;

    function renderEvent(event) {
        roomName.textContent = event.name;
        roomDeadline.textContent = new Date(event.deadline).toLocaleString("ru-RU");
        roomStatus.textContent = event.started ? "Жеребьёвка завершена" : "Ожидает запуска";
    }

    function renderParticipants(participants) {
        participantsList.replaceChildren();
        if (!participants.length) {
            participantsList.textContent = "Участников пока нет.";
            participantsList.classList.add("muted");
            return;
        }

        participantsList.classList.remove("muted");
        for (const participant of participants) {
            const row = document.createElement("div");
            const link = document.createElement("a");
            row.className = "participant";
            link.href = `/profile/${encodeURIComponent(participant.username)}`;
            link.textContent = participant.displayName;
            row.append(link);

            if (participant.owner) {
                const badge = document.createElement("span");
                badge.className = "badge-owner";
                badge.textContent = "Организатор";
                row.append(badge);
            }
            participantsList.append(row);
        }
    }

    function renderControls(event, user) {
        const isOwner = event.ownerId === user.id;
        startButton.classList.toggle("hidden", !isOwner || event.started);
        actionButton.classList.remove("hidden");
        actionButton.textContent = isOwner ? "Удалить игру" : "Выйти из игры";
        actionButton.dataset.action = isOwner ? "delete" : "leave";
    }

    function showWaiting() {
        assignmentCard.classList.add("hidden");
        waiting.classList.remove("hidden");
    }

    async function loadAssignment() {
        const assignment = await api.request(`/api/v1/events/${encodeURIComponent(eventId)}/participants/me/assignment`);
        waiting.classList.add("hidden");
        giftedUserName.textContent = assignment.displayName;
        giftedUserLink.href = `/profile/${encodeURIComponent(assignment.username)}`;
        assignmentCard.classList.remove("hidden");
    }

    async function loadRoom() {
        try {
            const [event, user, participants] = await Promise.all([
                api.request(`/api/v1/events/${encodeURIComponent(eventId)}`),
                api.request("/api/v1/users/me"),
                api.request(`/api/v1/events/${encodeURIComponent(eventId)}/participants`)
            ]);
            currentEvent = event;
            currentUser = user;
            renderEvent(event);
            renderParticipants(participants);
            renderControls(event, user);
            event.started ? await loadAssignment() : showWaiting();
        } catch (error) {
            roomError.textContent = error.message ?? "Не удалось загрузить игру.";
        }
    }

    copyButton?.addEventListener("click", async () => {
        const inviteLink = `${window.location.origin}/join/${encodeURIComponent(eventId)}`;
        try {
            await navigator.clipboard.writeText(inviteLink);
            const previousText = copyButton.textContent;
            copyButton.textContent = "Ссылка скопирована";
            setTimeout(() => copyButton.textContent = previousText, 1500);
        } catch {
            roomError.textContent = "Не удалось скопировать ссылку.";
        }
    });

    startButton?.addEventListener("click", async () => {
        startButton.disabled = true;
        roomError.textContent = "";
        try {
            currentEvent = await api.request(`/api/v1/events/${encodeURIComponent(eventId)}/start`, {method: "POST"});
            renderEvent(currentEvent);
            renderControls(currentEvent, currentUser);
            await loadAssignment();
        } catch (error) {
            roomError.textContent = error.message ?? "Не удалось начать игру.";
            startButton.disabled = false;
        }
    });

    actionButton?.addEventListener("click", async () => {
        const action = actionButton.dataset.action;
        if (!action) {
            return;
        }

        const message = action === "delete"
            ? "Вы уверены, что хотите удалить игру?"
            : "Вы уверены, что хотите выйти из игры?";
        if (!window.confirm(message)) {
            return;
        }

        actionButton.disabled = true;
        roomError.textContent = "";
        try {
            const url = action === "leave"
                ? `/api/v1/events/${encodeURIComponent(eventId)}/participants/me`
                : `/api/v1/events/${encodeURIComponent(eventId)}`;
            await api.request(url, {method: "DELETE"});
            window.location.href = "/games";
        } catch (error) {
            roomError.textContent = error.message ?? "Не удалось выполнить действие.";
            actionButton.disabled = false;
        }
    });

    loadRoom();
})();
