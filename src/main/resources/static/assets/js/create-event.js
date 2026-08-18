(() => {

    const form =
        document.getElementById("createEventForm");

    if (!form) {
        return;
    }

    const errorElement =
        document.getElementById("createEventError");

    form.addEventListener("submit", async (event) => {
        event.preventDefault();

        const name =
            document
                .getElementById("eventName")
                .value
                .trim();

        const deadline =
            document
                .getElementById("eventDeadline")
                .value;

        if (errorElement) {
            errorElement.textContent = "";
        }

        try {

            const createdEvent = await api.request(
                "/api/v1/events",
                {
                    method: "POST",

                    headers: {
                        "Content-Type": "application/json"
                    },

                    body: JSON.stringify({
                        name,
                        deadline
                    })
                }
            );

            window.location.href =
                `/room/${createdEvent.id}`;

        } catch (error) {

            console.error(error);

            if (errorElement) {
                errorElement.textContent =
                    error.message;
            }
        }
    });

})();