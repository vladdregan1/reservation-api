let currentPage = 0;
const pageSize = 5;
let currentEditId = null;
let currentReservations = [];

async function fetchReservations() {
    try {
        const response = await fetch(`http://localhost:8080/api/reservations?pageNo=${currentPage}&pageSize=${pageSize}`);
        if (!response.ok) throw new Error('Network response was not ok');

        const data = await response.json();
        currentReservations = data.content;
        const tableBody = document.getElementById('reservations-table-body');
        tableBody.innerHTML = '';

        data.content.forEach(reservation => {
            const row = `<tr>
        <td>#${reservation.id}</td>
        <td><strong>${reservation.customerName}</strong></td>
        <td>${reservation.email}</td>
        <td>${new Date(reservation.reservationTime).toLocaleString()}</td>
        <td>${reservation.numberOfGuests}</td>
        <td><span class="status-badge">${reservation.status}</span></td>
        <td>
            <button class="btn-edit" onclick="loadEditForm(${reservation.id})">Edit</button>
            <button class="btn-delete" onclick="deleteReservation(${reservation.id})">Cancel</button>
        </td>
    </tr>`;
            tableBody.innerHTML += row;
        });

        document.getElementById('pageInfo').innerText = `Page ${data.number + 1} of ${data.totalPages || 1}`;
        document.getElementById('prevBtn').disabled = data.first;
        document.getElementById('nextBtn').disabled = data.last;

    } catch (error) {
        console.error('Error fetching reservations:', error);
    }
}

function changePage(direction) {
    currentPage += direction;
    fetchReservations();
}

window.onload = fetchReservations;

window.onload = fetchReservations;

document.getElementById('reservation-form').addEventListener('submit', async function(event) {
    event.preventDefault();

    const payload = {
        customerName: document.getElementById('customerName').value,
        email: document.getElementById('email').value,
        reservationTime: document.getElementById('reservationTime').value,
        numberOfGuests: parseInt(document.getElementById('numberOfGuests').value)
    };

    const url = currentEditId
        ? `http://localhost:8080/api/reservations/${currentEditId}`
        : 'http://localhost:8080/api/reservations';

    const httpMethod = currentEditId ? 'PUT' : 'POST';

    try {
        const response = await fetch(url, {
            method: httpMethod,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            document.getElementById('reservation-form').reset();
            currentEditId = null;

            const submitBtn = document.querySelector('#reservation-form button');
            submitBtn.innerText = 'Submit Reservation';
            submitBtn.style.backgroundColor = '';

            fetchReservations();
        } else {
            const errorData = await response.json();
            alert('Eroare de validare: ' + JSON.stringify(errorData));
        }
    } catch (error) {
        console.error('Error:', error);
        alert("Can't connect to the server.");
    }
});

async function deleteReservation(id) {
    if (!confirm('Are you sure you want to cancel this reservation?')) {
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/api/reservations/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            fetchReservations();
        } else {
            alert('Failed to delete reservation.');
        }
    } catch (error) {
        console.error('Error deleting reservation:', error);
        alert('Server error.');
    }
}

function loadEditForm(id) {
    const reservation = currentReservations.find(r => r.id === id);
    if (!reservation) return;

    document.getElementById('customerName').value = reservation.customerName;
    document.getElementById('email').value = reservation.email;

    const dateStr = new Date(reservation.reservationTime).toISOString().slice(0, 16);
    document.getElementById('reservationTime').value = dateStr;

    document.getElementById('numberOfGuests').value = reservation.numberOfGuests;

    currentEditId = id;
    document.querySelector('#reservation-form button').innerText = 'Update Reservation';
    document.querySelector('#reservation-form button').style.backgroundColor = '#ffc107';

    window.scrollTo({ top: 0, behavior: 'smooth' });
}