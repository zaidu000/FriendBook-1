document.getElementById("search-box").addEventListener("input", async function() {
	const query = this.value.trim();
	const suggestionBox = document.getElementById("suggestions");

	if (query === "") {
		suggestionBox.innerHTML = "";
		suggestionBox.style.display = "none"; // <- Important to hide
		return;
	}

	try {
		const res = await fetch(`/api/search/users?query=${encodeURIComponent(query)}`);
		if (!res.ok) throw new Error("Failed to fetch");
		const users = await res.json();

		suggestionBox.innerHTML = "";
		if (users.length === 0) {
			suggestionBox.style.display = "none";
			return;
		}

		users.forEach(user => {
			const div = document.createElement("div");
			div.classList.add("suggestion-item");
			div.textContent = user.username;
			div.onclick = () => {
				suggestionBox.style.display = "none"; // hide
				loadUserModal(user.username);
			};
			suggestionBox.appendChild(div);
		});

		suggestionBox.style.display = "block"; // <- Make sure it's visible

	} catch (err) {
		console.error("Error loading suggestions:", err);
	}
});

async function loadUserModal(username) {
	const res = await fetch(`/api/user/${username}`);
	if (res.ok) {
		const data = await res.json();
		const html = `
		            <img src="/images/${data.profilePic}" width="80" style="border-radius:50%;" />
		            <h3>${data.name}</h3>
		            <p><strong>Username:</strong> ${data.username}</p>
		            <p><strong>Email:</strong> ${data.email}</p>
		            <p><strong>Followers:</strong> ${data.followers} | <strong>Following:</strong> ${data.following}</p>
		            <p><strong>Posts:</strong> ${data.postCount}</p>
		            <form method="post" action="/follow/${data.username}">
		                <button type="submit">${data.isFollowing ? "Unfollow" : "Follow"}</button>
		            </form>
		        `;

		document.getElementById("user-details").innerHTML = html;
		document.getElementById("user-modal").style.display = "block";
	} else {
		alert("Failed to load user");
	}
}


function closeModal() {
	document.getElementById("user-modal").style.display = "none";
}

async function submitComment(event, postId) {
	event.preventDefault();

	const form = event.target;
	const input = form.querySelector('input[name="text"]');
	const commentText = input.value;

	const response = await fetch(`/comments/${postId}`, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/x-www-form-urlencoded'
		},
		body: new URLSearchParams({ text: commentText })
	});

	if (response.ok) {
		input.value = '';
		location.reload(); // or dynamically update comments section
	} else {
		alert("Failed to add comment.");
	}
}

function toggleAddPostForm() {
	const form = document.getElementById("addPostForm");
	form.style.display = form.style.display === "none" ? "block" : "none";
}

// Handle create post
document.getElementById("createPostForm").addEventListener("submit", async function(e) {
	e.preventDefault();

	const form = e.target;
	const formData = new FormData(form);

	const res = await fetch("/posts/create", {
		method: "POST",
		body: formData
	});

	const msg = document.getElementById("postMessage");

	if (res.ok) {
		msg.innerText = "Post created successfully!";
		msg.style.color = "green";
		form.reset();
		setTimeout(() => {
			location.reload(); 
		}, 1000);
	} else {
		msg.innerText = "Failed to create post!";
		msg.style.color = "red";
	}
});

function showEditForm(btn) {
	const postId = btn.getAttribute("data-id");
	const form = document.getElementById(`edit-form-${postId}`);
	form.style.display = "block";
	form.scrollIntoView({ behavior: 'smooth', block: 'center' });
}

function cancelEdit(postId) {
	document.getElementById(`edit-form-${postId}`).style.display = "none";
}

// Handle Edit Submit (Prevent page reload)
async function handleEditSubmit(event, form) {
	event.preventDefault();
	const postId = form.action.split("/").pop(); 
	const formData = new FormData(form);
	const caption = formData.get("caption");

	const res = await fetch(`/posts/update/${postId}`, {
		method: "POST",
		body: new URLSearchParams({ caption })
	});

	if (res.ok) {
		alert("Post updated!");
		form.style.display = "none";
		location.reload(); 
	} else {
		alert("Failed to update post.");
	}
	return false;
}

function confirmDelete(btn) {
	const postId = btn.getAttribute("data-id");
	if (confirm("Are you sure you want to delete this post?")) {
		const form = document.createElement('form');
		form.method = 'POST';
		form.action = `/posts/delete/${postId}`;
		document.body.appendChild(form);
		form.submit();
	}
}

// Update favorites without page reload
document.getElementById("favoritesForm").addEventListener("submit", async function(e) {
	e.preventDefault();
	const formData = new FormData(e.target);
	const res = await fetch("/profile/update", {
		method: "POST",
		body: formData
	});
	if (res.ok) {
		alert("Favorites updated!");
	}
});

// Upload profile image without reload
document.getElementById("uploadForm").addEventListener("submit", async function(e) {
	e.preventDefault();
	const formData = new FormData(this);
	const res = await fetch("/profile/upload", {
		method: "POST",
		body: formData
	});
	if (res.ok) {
		location.reload();
	}
});

// Toggle Like
async function toggleLike(postId) {
	const res = await fetch(`/api/likes/${postId}`, { method: 'POST' });
	if (res.ok) {
		const newCount = await res.text();
		document.getElementById(`like-count-${postId}`).innerText = newCount;
	} else {
		alert("Failed to like/unlike");
	}
}