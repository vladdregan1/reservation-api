const API_URL = '/api/reservations';   // same server as this page, so no host needed

let currentPage = 0;
const pageSize = 5;
let currentEditId = null;
let currentReservations = [];

let authHeader = null;   // becomes "Basic xxxxx" after the admin logs in (kept only in memory)

function adminHeaders() {
    return { 'Content-Type': 'application/json', 'Authorization': authHeader };
}

function addCell(row, text) {
    const cell = document.createElement('td');
    cell.textContent = text;   // always plain text, never HTML (prevents XSS)
    row.appendChild(cell);
    return cell;
}

function addButton(cell, label, cssClass, onClick) {
    const button = document.createElement('button');
    button.textContent = label;
    button.className = cssClass;
    button.addEventListener('click', onClick);
    cell.appendChild(button);
}

async function fetchReservations() {
    try {
        const response = await fetch(`${API_URL}?pageNo=${currentPage}&pageSize=${pageSize}`, {
            headers: adminHeaders()
        });
        if (!response.ok) return false;

        const data = await response.json();
        currentReservations = data.content;

        const tableBody = document.getElementById('reservations-table-body');
        tableBody.replaceChildren();

        data.content.forEach(reservation => {
            const row = document.createElement('tr');
            addCell(row, '#' + reservation.id);
            addCell(row, reservation.customerName);
            addCell(row, reservation.email);
            addCell(row, new Date(reservation.reservationTime).toLocaleString());
            addCell(row, reservation.numberOfGuests);

            const statusCell = addCell(row, '');
            const badge = document.createElement('span');
            badge.className = 'status-badge status-' + reservation.status.toLowerCase();
            badge.textContent = reservation.status;
            statusCell.appendChild(badge);

            const actions = addCell(row, '');
            addButton(actions, 'Edit', 'btn-edit', () => loadEditForm(reservation.id));
            if (reservation.status === 'PENDING') {
                addButton(actions, 'Confirm', 'btn-confirm', () => changeStatus(reservation.id, 'confirm'));
            }
            if (reservation.status !== 'CANCELLED') {
                addButton(actions, 'Cancel', 'btn-cancel', () => changeStatus(reservation.id, 'cancel'));
            }
            addButton(actions, 'Delete', 'btn-delete', () => deleteReservation(reservation.id));

            tableBody.appendChild(row);
        });

        document.getElementById('pageInfo').textContent = `Page ${data.number + 1} of ${data.totalPages || 1}`;
        document.getElementById('prevBtn').disabled = data.first;
        document.getElementById('nextBtn').disabled = data.last;
        return true;

    } catch (error) {
        console.error('Error fetching reservations:', error);
        return false;
    }
}

function changePage(direction) {
    currentPage += direction;
    fetchReservations();
}

document.getElementById('prevBtn').addEventListener('click', () => changePage(-1));
document.getElementById('nextBtn').addEventListener('click', () => changePage(1));

document.getElementById('login-form').addEventListener('submit', async function (event) {
    event.preventDefault();
    const username = document.getElementById('adminUsername').value;
    const password = document.getElementById('adminPassword').value;

    authHeader = 'Basic ' + btoa(username + ':' + password);

    const ok = await fetchReservations();
    if (ok) {
        document.getElementById('login-section').style.display = 'none';
    } else {
        authHeader = null;
        document.getElementById('login-error').textContent = 'Wrong username or password.';
    }
});

document.getElementById('reservation-form').addEventListener('submit', async function(event) {
    event.preventDefault();

    const payload = {
        customerName: document.getElementById('customerName').value,
        email: document.getElementById('email').value,
        reservationTime: document.getElementById('reservationTime').value,
        numberOfGuests: parseInt(document.getElementById('numberOfGuests').value)
    };

    const url = currentEditId ? `${API_URL}/${currentEditId}` : API_URL;
    const httpMethod = currentEditId ? 'PUT' : 'POST';

    try {
        const response = await fetch(url, {
            method: httpMethod,
            // creating is public, editing needs the admin login
            headers: currentEditId ? adminHeaders() : { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            document.getElementById('reservation-form').reset();
            currentEditId = null;

            const submitBtn = document.querySelector('#reservation-form button');
            submitBtn.innerText = 'Submit Reservation';
            submitBtn.style.backgroundColor = '';

            if (authHeader) fetchReservations();
        } else {
            const errorData = await response.json();
            alert('Validation error: ' + JSON.stringify(errorData));
        }
    } catch (error) {
        console.error('Error:', error);
        alert("Can't connect to the server.");
    }
});

async function changeStatus(id, action) {
    try {
        const response = await fetch(`${API_URL}/${id}/${action}`, {
            method: 'PATCH',
            headers: adminHeaders()
        });

        if (response.ok) {
            fetchReservations();
        } else {
            const errorData = await response.json();
            alert(errorData.error);
        }
    } catch (error) {
        console.error('Error changing status:', error);
        alert('Server error.');
    }
}

async function deleteReservation(id) {
    if (!confirm('Delete this reservation permanently?')) {
        return;
    }

    try {
        const response = await fetch(`${API_URL}/${id}`, {
            method: 'DELETE',
            headers: adminHeaders()
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

    const dateStr = reservation.reservationTime.slice(0, 16);   // "2026-10-05T19:00"
    document.getElementById('reservationTime').value = dateStr;

    document.getElementById('numberOfGuests').value = reservation.numberOfGuests;

    currentEditId = id;
    document.querySelector('#reservation-form button').innerText = 'Update Reservation';
    document.querySelector('#reservation-form button').style.backgroundColor = '#ffc107';

    window.scrollTo({ top: 0, behavior: 'smooth' });
}
