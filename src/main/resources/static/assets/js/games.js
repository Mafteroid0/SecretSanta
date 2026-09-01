(() => {
    const gamesList = document.getElementById("gamesList");
    const joinForm = document.getElementById("joinEventForm");
    const joinInput = document.getElementById("joinEventId");
    const joinError = document.getElementById("joinGameError");
    const joinButton = document.getElementById("joinGameButton");
    const uuidPattern = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

    function extractEventId(value) {
        const input = value.trim();
        if (uuidPattern.test(input)) {
            return input;
        }
        return input.match(/(?:^|\/)join\/([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})(?:\/|[?#]|$)/i)?.[1] ?? null;
    }

    function renderEvents(events) {
        gamesList.replaceChildren();
        if (!events.length) {
            gamesList.textContent = "Вы пока не участвуете ни в одной игре.";
            gamesList.classList.add("muted");
            return;
        }

        gamesList.classList.remove("muted");
        for (const event of events) {
            const link = document.createElement("a");
            const name = document.createElement("div");
            const deadline = document.createElement("div");
            link.className = "game-card";
            link.href = `/room/${event.id}`;
            name.className = "game-name";
            name.textContent = event.name;
            deadline.className = "game-deadline";
            deadline.textContent = `До ${new Date(event.deadline).toLocaleString("ru-RU")}`;
            link.append(name, deadline);
            gamesList.append(link);
        }
    }

    async function loadEvents() {
        try {
            renderEvents(await api.request("/api/v1/events"));
        } catch {
            gamesList.textContent = "Не удалось загрузить игры.";
            gamesList.classList.add("error");
        }
    }

    document.querySelectorAll("[data-dialog-open]").forEach(button => {
        button.addEventListener("click", () => document.getElementById(button.dataset.dialogOpen)?.showModal());
    });

    document.querySelectorAll("[data-dialog-close]").forEach(button => {
        button.addEventListener("click", () => button.closest("dialog")?.close());
    });

    joinForm?.addEventListener("submit", async event => {
        event.preventDefault();
        const eventId = extractEventId(joinInput.value);
        joinError.textContent = "";

        if (!eventId) {
            joinError.textContent = "Введите корректный ID игры или ссылку-приглашение.";
            return;
        }

        joinButton.disabled = true;
        try {
            await api.request(`/api/v1/events/${encodeURIComponent(eventId)}/join`, {method: "POST"});
            window.location.href = `/room/${encodeURIComponent(eventId)}`;
        } catch (error) {
            const messages = {
                400: "Некорректный ID игры.",
                404: "Игра с таким ID не найдена.",
                409: error.message ?? "Вы не можете присоединиться к этой игре."
            };
            joinError.textContent = messages[error.status] ?? "Не удалось присоединиться к игре.";
            joinButton.disabled = false;
        }
    });

    loadEvents();
})();
