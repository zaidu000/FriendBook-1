document.getElementById("signupForm").addEventListener("submit", function(e) {
	e.preventDefault();

	const recaptchaResponse = grecaptcha.getResponse();
	if (!recaptchaResponse) {
		document.getElementById("message").innerText = "Please complete the CAPTCHA.";
		document.getElementById("message").style.color = "red";
		return;
	}

	const data = {
		fullName: document.getElementById("fullName").value,
		email: document.getElementById("email").value,
		password: document.getElementById("password").value,
		captchaToken: recaptchaResponse
	};

	fetch("/api/signup", {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify(data)
	})
		.then(res => res.json())
		.then(response => {
			const msg = document.getElementById("message");
			msg.style.color = response.success ? "green" : "red";
			msg.innerText = response.message;
			if (response.success) setTimeout(() => window.location.href = "/login", 1500);
		});
});