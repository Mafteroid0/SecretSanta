(() => {
    const content = document.getElementById("joinGameContent");
    const name = document.getElementById("joinGameName");
    const deadline = document.getElementById("joinGameDeadline");
    const button = document.getElementById("joinGameButton");
    const loading = document.getElementById("joinGameLoading");
    const errorElement = document.getElementById("joinGameError");
    const eventId = window.location.pathname.split("/").filter(Boolean).at(-1);

    function showError(message) {
        errorElement.textContent = message;
        errorElement.classList.remove("hidden");
    }

    async function loadGame() {
        try {
            const game = await api.request(`/api/v1/events/${encodeURIComponent(eventId)}`);
            name.textContent = game.name;
            deadline.textContent = new Intl.DateTimeFormat("ru-RU", {
                day: "2-digit",
                month: "2-digit",
                year: "numeric"
            }).format(new Date(game.deadline));
            loading.classList.add("hidden");
            content.classList.remove("hidden");
        } catch (error) {
            loading.classList.add("hidden");
            showError(error.status === 404 ? "Игра не найдена." : error.message ?? "Не удалось загрузить игру.");
        }
    }

    button?.addEventListener("click", async () => {
        button.disabled = true;
        errorElement.classList.add("hidden");
        errorElement.textContent = "";

        try {
            await api.request(`/api/v1/events/${encodeURIComponent(eventId)}/join`, {method: "POST"});
            window.location.href = `/room/${encodeURIComponent(eventId)}`;
        } catch (error) {
            const messages = {
                404: "Игра не найдена.",
                409: "Вы уже участвуете в этой игре."
            };
            showError(messages[error.status] ?? error.message ?? "Не удалось присоединиться к игре.");
            button.disabled = false;
        }
    });

    loadGame();
})();
