document.addEventListener("DOMContentLoaded", function() {
	const btn = document.querySelector(".send-request-btn");

	if (btn && btn.textContent === "Send Request") {
		btn.addEventListener("click", function() {
			const username = this.getAttribute("data-username");

			fetch(`/friend-request/send/${username}`, {
				method: "POST"
			})
				.then(res => {
					if (res.ok) {
						this.textContent = "Request Sent";
						this.disabled = true;
					} else {
						alert("Failed to send friend request.");
					}
				})
				.catch(error => {
					console.error("Error sending friend request:", error);
					alert("Something went wrong.");
				});
		});
	}
});