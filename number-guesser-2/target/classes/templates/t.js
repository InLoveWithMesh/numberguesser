
function restart() {
    fetch("http://localhost:8080/start-game", { method: 'GET' })
        .catch(error => console.log('error', error));
    window.location.href = "/game";
}

function guessRequest(event) {
    event.preventDefault(); // Das Standardverhalten des Submit-Buttons unterdrücken
    var guessednumber = document.getElementById("guess").value;
    console.log("Guessed Number: " + guessednumber);

    fetch("http://localhost:8080/isfinished", { method: 'GET' })
        .then(response => response.json())
        .then(data => {
          if (data === true) {
            fetch("http://localhost:8080/start-game", { method: 'GET' })
              .catch(error => console.log('error', error));
          }
        })
        .catch(error => {
          console.log("Fehler beim Abrufen der Daten:", error);
        });

    fetch("http://localhost:8080/guess?number=" + guessednumber, { method: 'GET' })
        .then(response => response.json())
        .then(data => {
            console.log(data);
            document.getElementById("log").innerHTML = data.requestLog;
            document.getElementById("upper").innerHTML = data.upperBorder;
            document.getElementById("lower").innerHTML = data.lowerBorder;
            document.getElementById("guessed").innerHTML = data.guessedNumber;
            document.getElementById("message").innerHTML = data.message;

            if(data.hit == true){document.getElementById("message").style.color = "green";}
            else {document.getElementById("message").style.color = "yellow";}
        })
        .catch(error => console.log('error', error));

    // Größe des Formulars ändern
    var form = document.getElementById("myForm");
    form.classList.remove("form-small");
    form.classList.add("form-large");

    // Sichtbarkeit der Tabelle ändern
    document.getElementById("myTable").style.visibility = "visible";
    document.getElementById("message").style.visibility = "visible";
    document.getElementById("log").style.visibility = "visible";
    document.getElementById("log-label").style.visibility = "visible";
    document.getElementById("restartbutton").style.visibility = "visible";
}
var input = document.getElementById("guess");
input.addEventListener("keypress", function(event) {
  if (event.key === "Enter") {
    event.preventDefault();
    document.getElementById("submitbutton").click();
  }
});

function logout() {
    window.location.href = "/logout?logout";
  }
