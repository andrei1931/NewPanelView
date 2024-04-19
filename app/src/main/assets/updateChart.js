function updateChart(chartData) {
    console.log('Received data:', chartData);
    if (chartData) {
        const ctx = document.getElementById('myChart').getContext('2d');
        const labels = chartData.map(item => item.timestamp);
        const data = chartData.map(item => item.power);

        new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Power Generation',
                    data: data,
                    backgroundColor: 'rgba(75, 192, 192, 0.2)',
                    borderColor: 'rgba(75, 192, 192, 1)',
                    borderWidth: 1
                }]
            },
            options: {
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });

        return true;
    } else {
        return false;
    }
}