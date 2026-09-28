async function fetchReservations() {
    try {
        const response = await fetch('http://localhost:8080/api/reservations');

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }

        const data = await response.json();
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
            </tr>`;
            tableBody.innerHTML += row;
        });

    } catch (error) {
        console.error('Error fetching reservations:', error);
        document.getElementById('reservations-table-body').innerHTML =
            `<tr><td colspan="6" style="color:red;">Error connecting to API. Is Spring Boot running?</td></tr>`;
    }
}

window.onload = fetchReservations;

document.getElementById('reservation-form').addEventListener('submit', async function(event) {
    event.preventDefault();

    const payload = {
        customerName: document.getElementById('customerName').value,
        email: document.getElementById('email').value,
        reservationTime: document.getElementById('reservationTime').value,
        numberOfGuests: parseInt(document.getElementById('numberOfGuests').value)
    };

    try {
        const response = await fetch('http://localhost:8080/api/reservations', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            document.getElementById('reservation-form').reset();
            fetchReservations();
        } else {
            const errorData = await response.json();
            alert('Eroare de validare: ' + JSON.stringify(errorData));
        }
    } catch (error) {
        console.error('Error submitting reservation:', error);
        alert('Nu s-a putut conecta la server.');
    }
});