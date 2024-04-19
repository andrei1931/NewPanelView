// Importați biblioteca jsdom
const { JSDOM } = require('jsdom');

// Creați un mediu DOM simulat
const dom = new JSDOM('<!DOCTYPE html><html><body></body></html>');

// Atribuiți obiectul window global pentru a accesa DOM-ul simulat
global.window = dom.window;
global.document = dom.window.document;

// Importați funcția updateChart și asigurați-vă că este disponibilă în mediu global
const { updateChart } = require('./updateChart.js');

// Importați biblioteca de aserțiuni
const assert = require('assert');

// Definiți și rulați testul pentru funcția updateChart
describe('updateChart', function() {
    it('should update the chart with provided data', function() {
        // Define the test data
        const data = [537.89, 775.16, 601.26, 707.32, 709.98];

        // Call the updateChart function
        const result = updateChart(data);

        // Assert that the chart was updated successfully
        // You can customize this assertion based on how your chart updates
        assert.strictEqual(result, true);
    });
});
