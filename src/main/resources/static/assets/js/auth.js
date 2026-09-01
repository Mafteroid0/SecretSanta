(() => {
    const form = document.querySelector("[data-auth-form]");
    const notice = document.querySelector("[data-form-notice]");
    if (!form || !notice) {
        return;
    }

    const messages = {
        username: {
            blank: "Логин не должен быть пустым.",
            short: "Логин должен содержать не менее 3 символов.",
            long: "Логин должен содержать не более 50 символов."
        },
        displayName: {
            blank: "Имя не должно быть пустым.",
            long: "Имя должно содержать не более 100 символов."
        },
        password: {
            blank: "Пароль не должен быть пустым.",
            short: "Пароль должен содержать не менее 8 символов.",
            long: "Пароль должен содержать не более 72 символов."
        }
    };

    function fieldError(field) {
        const value = field.name === "password" ? field.value : field.value.trim();
        const rules = messages[field.name];
        if (!rules) {
            return null;
        }
        if (!value) {
            return rules.blank;
        }
        if (field.minLength > 0 && value.length < field.minLength) {
            return rules.short;
        }
        if (field.maxLength > 0 && value.length > field.maxLength) {
            return rules.long;
        }
        return null;
    }

    function showErrors(errors) {
        notice.replaceChildren();
        if (!errors.length) {
            notice.classList.remove("visible");
            return;
        }
        const list = document.createElement("ul");
        for (const message of errors) {
            const item = document.createElement("li");
            item.textContent = message;
            list.append(item);
        }
        notice.append(list);
        notice.classList.add("visible");
    }

    form.addEventListener("submit", event => {
        const fields = [...form.querySelectorAll("input[name]")];
        const errors = fields.map(fieldError).filter(Boolean);
        if (errors.length) {
            event.preventDefault();
            showErrors(errors);
            fields.find(field => fieldError(field))?.focus();
        } else {
            showErrors([]);
        }
    });

    form.addEventListener("invalid", event => {
        event.preventDefault();
        const fields = [...form.querySelectorAll("input[name]")];
        showErrors(fields.map(fieldError).filter(Boolean));
        fields.find(field => fieldError(field))?.focus();
    }, true);

    form.addEventListener("input", () => {
        if (notice.classList.contains("visible")) {
            const errors = [...form.querySelectorAll("input[name]")].map(fieldError).filter(Boolean);
            showErrors(errors);
        }
    });

    if (notice.textContent.trim()) {
        notice.classList.add("visible");
    }
})();
