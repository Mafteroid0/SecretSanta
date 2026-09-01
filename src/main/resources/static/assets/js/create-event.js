(() => {
    const form = document.getElementById("createEventForm");
    if (!form) {
        return;
    }

    const errorElement = document.getElementById("createEventError");
    const submitButton = form.querySelector("button[type='submit']");

    form.addEventListener("submit", async event => {
        event.preventDefault();
        errorElement.textContent = "";
        submitButton.disabled = true;

        try {
            const createdEvent = await api.request("/api/v1/events", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({
                    name: document.getElementById("eventName").value.trim(),
                    deadline: document.getElementById("eventDeadline").value
                })
            });
            window.location.href = `/room/${createdEvent.id}`;
        } catch (error) {
            errorElement.textContent = error.message;
            submitButton.disabled = false;
        }
    });
})();
