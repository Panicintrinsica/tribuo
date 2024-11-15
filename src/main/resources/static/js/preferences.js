document.addEventListener('DOMContentLoaded', () => {
    initPreferenceTable();
    wireInputButtons();
});

function getPrefData() {
    return fetch('/data/prefData.json').then(response => response.json())
}

// --------------------------------------------------------------------------------------------------------------------
// Preference Summary
// --------------------------------------------------------------------------------------------------------------------
/**
 * Initializes the preference table by fetching the preference data,
 * initializing the summary table with the data, and setting up a periodic
 * refresh of the table every 30 seconds.
 *
 * @return {void}
 *     This function does not return a value.
 */
function initPreferenceTable() {
    getPrefData().then(data => {
        initSummaryTable(data);
        refreshTable();
        setInterval(() => refreshTable(), 30000); // Refresh table every 30 seconds.
    });
}

/**
 * Initializes the seating summary table with the given preference data.
 *
 * @param {Object} prefData An object containing seating preference data for each stand.
 * Each key is a stand identifier, and each value is an object with details including
 * the number of available seats and the price per seat.
 * @return {void} Does not return a value.
 */
function initSummaryTable(prefData) {
    const tbody = document.getElementById('seating-table');
    tbody.innerHTML = ''; // Clear existing content

    Object.keys(prefData).forEach(stand => {
        const standDetail = prefData[stand];
        const row = document.createElement('tr');
        row.dataset.stand = stand;

        row.innerHTML = `
            <th scope="row">${stand}</th>
            <td class="available-seats">${standDetail.available.length}</td>
            <td class="preferred-seats">0</td>
            <td class="price">${standDetail.price}</td>
            <td class="estimated-earnings">0</td>
        `;
        tbody.appendChild(row);
    });
}

/**
 * Updates the summary table with the number of preferred seats and estimated earnings
 * for each stand based on the provided preference data and fan information.
 *
 * @param {Object} prefData - An object mapping each stand to its preferences.
 * @param {Array} fans - An array of fan objects, each containing fan details and their preferred stand.
 * @return {void}
 */
function updateSummaryTable(prefData, fans) {
    const seatingTable = document.getElementById('seating-table');
    Object.keys(prefData).forEach(stand => {
        const row = seatingTable.querySelector(`tr[data-stand="${stand}"]`);
        const preferredSeats = fans.filter(fan => fan.preferredStand === stand).length;
        const availableSeats = parseInt(row.querySelector('.available-seats').textContent);
        const price = parseFloat(row.querySelector('.price').textContent);

        row.querySelector('.preferred-seats').textContent = preferredSeats;

        const estimatedEarnings = Math.min(preferredSeats, availableSeats) * price;
        row.querySelector('.estimated-earnings').textContent = estimatedEarnings.toFixed(2);
    });
    calculateSummary(prefData, fans);
}

/**
 * Fetches the list of fans from the server.
 *
 * @return {Promise<Array>} A promise that resolves to an array of fans.
 */
function getFans() {
    return fetch('/api/fans').then(response => response.json())
}
/**
 * Refreshes and updates the summary table with the latest preference data
 * and fan information by fetching these details asynchronously.
 *
 * @return {void} This function does not return a value.
 */
function refreshTable() {
    getPrefData().then(prefData => {
        getFans().then(fans => {
            updateSummaryTable(prefData, fans);
        }).catch(error => console.error('Error fetching fans data:', error));
    });
}

/**
 * Calculates and updates the summary of seating and earnings for an event.
 *
 * @param {Array} prefData - Array containing preferred data for seats.
 * @param {Array} fans - Array containing information about fans.
 * @return {void}
 */
function calculateSummary(prefData, fans) {
    let totalEarnings = 0;
    let totalPreferredSeats = 0;
    let totalFilledSeats = 0;
    let totalAvailableSeats = 0;

    const seatingTable = document.getElementById('seating-table');

    seatingTable.querySelectorAll('tr').forEach(row => {
        const availableSeats = parseInt(row.cells[1].textContent);
        const preferredSeats = parseInt(row.querySelector('.preferred-seats').textContent);
        const earnings = parseFloat(row.querySelector('.estimated-earnings').textContent);

        totalEarnings += earnings;
        totalPreferredSeats += preferredSeats;
        totalFilledSeats += Math.min(preferredSeats, availableSeats);
        totalAvailableSeats += availableSeats;
    });

    document.getElementById('total-earnings').textContent = `$${totalEarnings.toFixed(2)}`;
    document.getElementById('total-filled-seats').textContent = totalFilledSeats;
    document.getElementById('total-available-seats').textContent = totalAvailableSeats; // Assuming you have an element with this ID
    document.getElementById('registrations').textContent = fans.length;
}

// --------------------------------------------------------------------------------------------------------------------
// Add Form
// --------------------------------------------------------------------------------------------------------------------

document.getElementById('preferenceForm').addEventListener('submit', (event) => {
    event.preventDefault(); // Prevent default form submission

    // Helper function to check if a field is empty
    const isEmpty = (value) => !value || value.trim() === '';

    // Helper function to clean phone numbers
    const cleanPhoneNumber = (phone) => phone.replace(/[^\d]/g, '');

    // Helper function to get current timestamp in the format of yyyy-MM-dd HH:mm:ss.SSS
    const getCurrentTimestamp = () => {
        const now = new Date();
        const pad = (num) => num.toString().padStart(2, '0');
        const padMillis = (num) => num.toString().padStart(3, '0');

        return `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())} ${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}.${padMillis(now.getMilliseconds())}`;
    };

    // Gather data from the form fields
    const newFan = {
        firstName: document.getElementById('firstName').value,
        lastName: document.getElementById('lastName').value,
        email: document.getElementById('email').value,
        phone: cleanPhoneNumber(document.getElementById('phone').value),
        firstOccupation: document.getElementById('firstOccupation').value,
        preferredStand: document.getElementById('preferredStand').value,
        reservationTime: document.getElementById('reservationDate').value
    };

    // Replace empty timestamp with the current timestamp
    if (isEmpty(newFan.reservationTime)) {
        newFan.reservationTime = getCurrentTimestamp();
    }

    // Check if any field is empty
    for (const [key, value] of Object.entries(newFan)) {
        if (isEmpty(value) && key !== 'reservationTime') {
            alert(`Please fill out the ${key} field.`);
            return; // Stop the form submission
        }
    }

    // Check if phone number is valid (10 digits)
    if (!/^\d{10,}$/.test(newFan.phone.replace(/[()\-\s]/g, ''))) {
        alert('Please enter a valid phone number.');
        return; // Stop the form submission
    }

    // Send the data to the backend
    fetch('/api/fans', { // Use the same endpoint as for importing
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newFan)
    })
        .then(response => response.json())
        .then(result => {
            if (result.success) {
                alert('Fan added successfully!');
                refreshTable();
                // Optionally: Clear the form fields after successful submission
                document.getElementById('preferenceForm').reset();
            } else {
                alert('Failed to add fan.');
            }
        })
        .catch(error => {
            console.error('Error adding fan:', error);
            alert('Error: ' + error);
        });
});

// --------------------------------------------------------------------------------------------------------------------
// Import Preferences
// --------------------------------------------------------------------------------------------------------------------

/**
 * Wires up the input buttons by adding event listeners to them.
 * The 'importBtn' when clicked, triggers a click event on 'fileInput'.
 * The 'fileInput' when changed, triggers the handleFileImport function.
 *
 * @return {void} This function does not return a value.
 */
function wireInputButtons() {
    document.getElementById('importBtn').addEventListener('click', () => {
        document.getElementById('fileInput').click();
    });

    document.getElementById('fileInput').addEventListener('change', handleFileImport);
}

/**
 * Handles the import of a JSON file, reads its content, and processes the JSON data.
 *
 * @param {Event} event - The file input change event containing the selected file.
 * @return {void} - This function does not return a value.
 */
function handleFileImport(event) {
    const file = event.target.files[0];
    if (file && file.type === "application/json") {
        const reader = new FileReader();
        reader.onload = (e) => {
            try {
                const jsonData = JSON.parse(e.target.result.trim());
                processJsonData(jsonData);
            } catch (error) {
                console.error('Error parsing JSON:', error);
                alert('Invalid JSON file.');
            }
        };
        reader.readAsText(file);
    } else {
        console.warn('Selected file is not a JSON file');
        alert('Please select a .json file.');
    }
}

/**
 * Processes JSON data to find duplicates and display import results.
 *
 * @param {Object} jsonData - The JSON data to be processed.
 * @return {void} This function does not return a value.
 */
function processJsonData(jsonData) {
    const duplicates = findDuplicatesInImportData(jsonData);
    console.log('Duplicates:', duplicates);
    if (duplicates.length > 0) {
        alert('Duplicated records found: ' + JSON.stringify(duplicates));
        return;
    }
    showImportResults(jsonData);
}

/**
 * Identifies and returns a list of duplicate names from the provided import data.
 *
 * @param {Array<Object>} data - An array of objects representing the imported data.
 * Each object should have `firstName` and `lastName` properties.
 * @return {Array<string>} An array of strings where each string represents a full name
 * (first and last name concatenated) that appears more than once in the import data.
 */
function findDuplicatesInImportData(data) {
    const nameMap = new Map();
    const duplicates = [];

    data.forEach(record => {
        const fullName = `${record.firstName} ${record.lastName}`;

        if (nameMap.has(fullName)) {
            duplicates.push(fullName);
        } else {
            nameMap.set(fullName, true);
        }
    });

    return duplicates;
}

/**
 * Displays import results in a table and shows a modal dialog for user actions on the imported data.
 *
 * @param {Object[]} data - An array of data records to be displayed and potentially imported.
 * @param {string} data[].firstName - The first name of the record.
 * @param {string} data[].lastName - The last name of the record.
 * @param {string} data[].email - The email of the record.
 * @param {string} data[].phone - The phone number of the record.
 * @param {string} data[].firstOccupation - The first occupation of the record.
 * @param {string} data[].preferredStand - The preferred stand of the record.
 * @param {string} data[].reservationTime - The reservation time of the record.
 *
 * @return {void}
 */
function showImportResults(data) {
    const tbody = $('#dataBody');
    tbody.empty(); // Clear previous data
    data.forEach(record => {
        const row = $('<tr />').html(`
            <td>${record.firstName}</td>
            <td>${record.lastName}</td>
            <td>${record.email}</td>
            <td>${record.phone}</td>
            <td>${record.firstOccupation}</td>
            <td>${record.preferredStand}</td>
            <td>${record.reservationTime}</td>
        `);
        tbody.append(row);
    });

    const dataModalElement = document.getElementById('dataModal');
    if (!dataModalElement) {
        console.error('Modal element not found!');
        return;
    }
    const dataModal = new bootstrap.Modal(dataModalElement, {});
    dataModal.show();

    $('#submitBtn').off('click').on('click', function() {
        submitImportResults(data, 'replace'); // Import and Replace
        dataModal.hide();
    });

    // Assuming you have a button with the ID 'appendBtn' for Import & Append
    $('#appendBtn').off('click').on('click', function() {
        submitImportResults(data, 'append'); // Import and Append
        dataModal.hide();
    });
}

/**
 * Submits import results to the specified endpoint.
 *
 * @param {Object} data - The data to be imported.
 * @param {string} importType - The type of import being performed.
 *
 * @return {Promise} A promise that resolves with the server response.
 */
function submitImportResults(data, importType) {
    fetch('/api/fans', { // Update endpoint if necessary
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Import-Type': importType
        },
        body: JSON.stringify(data)
    })
        .then(response => response.json())
        .then(result => {
            if (result.success) {
                refreshTable();
                alert(result.message);
            } else {
                alert('Failed to update preferences.');
            }
        })
        .catch(error => {
            console.error('Error submitting data:', error);
            alert('Error: ' + error);
        });
}


function handleRewardClick() {
    fetch('http://localhost:8080/rewards/trigger', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.text())
    .then(data => {
        console.log(data);
        window.location.href = 'http://localhost:8080/rewards';
    })
    .catch(error => {
        console.error('Error:', error);
    });
}