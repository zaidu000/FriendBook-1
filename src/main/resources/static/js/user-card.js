document.addEventListener("DOMContentLoaded", function() {
	// --- Friend Request Logic ---
	const requestBtn = document.querySelector(".send-request-btn");
	if (requestBtn && requestBtn.textContent === "Send Request") {
		requestBtn.addEventListener("click", function() {
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
				.catch(err => console.error("Error:", err));
		});
	}

	// --- Follow/Unfollow Logic ---
	const followBtn = document.querySelector(".follow-btn");
	if (followBtn) {
		followBtn.addEventListener("click", function() {
			const targetUserId = this.getAttribute("data-userid"); 
			const isFollowing = this.textContent.trim() === "Unfollow";

			if (!targetUserId) {
				console.error("Missing target userId on button!");
				return;
			}

			if (isFollowing) {
				// --- Unfollow ---
				fetch(`/api/follow/${targetUserId}`, {
					method: "DELETE"
				})
					.then(res => {
						if (res.ok) {
							this.textContent = "Follow";
						} else {
							alert("Failed to unfollow user.");
						}
					})
					.catch(err => console.error("Error:", err));
			} else {
				// --- Follow ---
				fetch(`/api/follow/${targetUserId}`, {
					method: "POST"
				})
					.then(res => {
						if (res.ok) {
							this.textContent = "Unfollow";
						} else {
							alert("Failed to follow user.");
						}
					})
					.catch(err => console.error("Error:", err));
			}
		});
	}
});

