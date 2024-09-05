/* gestion des lien actifs sans jquery */

document.addEventListener('DOMContentLoaded', function () {
    let path = window.location.pathname;

    const home = document.getElementById("home");
    const profile = document.getElementById("profile");
    const addConnection = document.getElementById("addConnection");

    if (path === "/transfer" || path === "/home") {
        home.classList.add("active");
    } else if (path === "/profile") {
        profile.classList.add("active");
    } else if (path === "/addConnection") {
        addConnection.classList.add("active");
    }
})