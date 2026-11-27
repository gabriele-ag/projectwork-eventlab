const API_BASE_URL = "http://localhost:8081/api";

const eventsList = document.getElementById("eventsList");
const bookingEventSelect = document.getElementById("bookingEventSelect");
const feedbackMessage = document.getElementById("feedbackMessage");

let events = [];

function showFeedback(message, type = "info") {
  if (!feedbackMessage) return;
  feedbackMessage.textContent = message;
  feedbackMessage.className = `feedback ${type}`;
}

function hideFeedback() {
  if (!feedbackMessage) return;
  feedbackMessage.className = "feedback hidden";
  feedbackMessage.textContent = "";
}

function formatDate(isoString) {
  if (!isoString) return "Data da definire";
  const date = new Date(isoString);
  if (Number.isNaN(date.getTime())) return isoString;
  return date.toLocaleString("it-IT", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit"
  });
}

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;");
}

function renderEvents() {
  if (!eventsList) return;

  eventsList.innerHTML = "";
  if (bookingEventSelect) {
    bookingEventSelect.innerHTML = '<option value="">Seleziona Evento</option>';
  }

  if (events.length === 0) {
    eventsList.innerHTML = '<p class="empty-state">Nessun evento disponibile al momento.</p>';
    return;
  }

  events.forEach((event) => {
    const card = document.createElement("article");
    card.className = "event-card";

    card.innerHTML = `
      <div class="event-body">
        <div class="event-label">Titolo Evento:</div>
        <div class="event-value">${escapeHtml(event.title)}</div>

        ${event.description ? `
          <div class="event-label">Descrizione:</div>
          <div class="event-description">${escapeHtml(event.description)}</div>
        ` : ""}

        <div class="event-label">Data:</div>
        <div class="event-value">${escapeHtml(formatDate(event.eventDate))}</div>

        <div class="event-label">Luogo:</div>
        <div class="event-value">${escapeHtml(event.location)}</div>

        <div class="event-seats">
          <strong>Capienza massima:</strong> ${escapeHtml(event.maxSeats)}
        </div>
      </div>

      <button type="button" class="btn event-book-btn" data-event-id="${escapeHtml(event.id)}">
        Prenota
      </button>
    `;

    const bookBtn = card.querySelector(".event-book-btn");
    bookBtn.addEventListener("click", () => {
      if (bookingEventSelect) {
        bookingEventSelect.value = String(event.id);
      }
      const nameInput = document.getElementById("participantName");
      if (nameInput) {
        nameInput.focus();
      }
    });

    eventsList.appendChild(card);

    if (bookingEventSelect) {
      const option = document.createElement("option");
      option.value = String(event.id);
      option.textContent = `${event.title} (${event.location})`;
      bookingEventSelect.appendChild(option);
    }
  });
}

async function fetchEvents() {
  hideFeedback();
  if (eventsList) {
    eventsList.innerHTML = '<p class="loading-state">Caricamento eventi in corso...</p>';
  }

  try {
    const response = await fetch(`${API_BASE_URL}/events`);
    if (!response.ok) {
      throw new Error(`Errore HTTP ${response.status}`);
    }

    const data = await response.json();
    events = Array.isArray(data) ? data : [];
    renderEvents();
  } catch (error) {
    console.error("Errore nel recupero degli eventi:", error);
    if (eventsList) {
      eventsList.innerHTML = '<p class="error-state">Impossibile caricare gli eventi dal server.</p>';
    }
    showFeedback(
      "Impossibile collegarsi a event-service su http://localhost:8081. Verifica che il servizio sia avviato e che CORS sia attivo.",
      "error"
    );
  }
}

document.addEventListener("DOMContentLoaded", () => {
  fetchEvents();
});