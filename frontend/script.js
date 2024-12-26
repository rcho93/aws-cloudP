function toggleMenu() {
  const menu = document.querySelector(".menu-links");
  const icon = document.querySelector(".menu-icon");
  const picContainer = document.querySelector(".section__pic-container");

  menu.classList.toggle("open");
  icon.classList.toggle("open");

  if (menu.classList.contains("open")) {
    picContainer.style.display = "none";
  } else {
    picContainer.style.display = "block";
  }
}

document.addEventListener("DOMContentLoaded", () => {
  const navLinks = document.querySelectorAll("nav ul li a");
  const sections = document.querySelectorAll("section");
  const closeButtons = document.querySelectorAll(".close-btn");
  const menu = document.querySelector(".menu-links");
  const icon = document.querySelector(".menu-icon");
  const picContainer = document.querySelector(".section__pic-container");

  const hideAllSections = () => {
    sections.forEach((section) => {
      section.style.display = "none";
    });
  };

  const showSection = (id) => {
    hideAllSections();
    const targetSection = document.getElementById(id);
    if (targetSection) {
      targetSection.style.display = "block";
    }
  };

  hideAllSections();
  document.getElementById("profile").style.display = "flex";
  document.getElementById("profile").style.justifyContent = "center";

  navLinks.forEach((link) => {
    link.addEventListener("click", (e) => {
      const target = link.getAttribute("data-target");
      if (target) {
        showSection(target);

        menu.classList.remove("open");
        icon.classList.remove("open");
      }
    });
  });

  closeButtons.forEach((btn) => {
    btn.addEventListener("click", () => {
      hideAllSections();
      document.getElementById("profile").style.display = "flex";
      document.getElementById("profile").style.justifyContent = "center";
      picContainer.style.display = "block";
    });
  });

  const cntElement = document.getElementById("visitor-cnt");
  // call the API Gateway endpoint
  fetch("https://6w5sx4fued.execute-api.us-west-2.amazonaws.com/prod/product")
    .then((response) => response.json())
    .then((data) => {
      const count = data.body;
      console.log("count: ", count);
      cntElement.textContent =
        "You are the " + count + "th visitor to my website";
    })
    .catch((e) => console.error("Error fetching the visitor count: ", e));
});
