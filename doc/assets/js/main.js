(function () {
  var btn = document.querySelector(".nav-toggle");
  var nav = document.querySelector(".doc-nav");
  if (btn && nav) {
    btn.addEventListener("click", function () {
      nav.classList.toggle("is-open");
    });
  }
})();
