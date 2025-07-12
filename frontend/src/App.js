import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useEffect, useState, useCallback } from 'react';
import { Line } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend, } from 'chart.js';
import './App.css';
ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend);
const INITIAL_SERVERS = 1;
const INITIAL_SELF_CHECKS = 0;
const INITIAL_QMAX = 1;
const INITIAL_NUM_CUSTOMERS = 10;
const INITIAL_MEAN_ARRIVAL_INTERVAL = 0.5;
const INITIAL_MEAN_SERVICE_TIME = 1.0;
const INITIAL_MEAN_REST_TIME = 0.0;
function App() {
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    // Form state
    const [servers, setServers] = useState(INITIAL_SERVERS);
    const [selfChecks, setSelfChecks] = useState(INITIAL_SELF_CHECKS);
    const [qmax, setQmax] = useState(INITIAL_QMAX);
    const [numCustomers, setNumCustomers] = useState(INITIAL_NUM_CUSTOMERS);
    const [meanArrivalInterval, setMeanArrivalInterval] = useState(INITIAL_MEAN_ARRIVAL_INTERVAL);
    const [meanServiceTime, setMeanServiceTime] = useState(INITIAL_MEAN_SERVICE_TIME);
    const [meanRestTime, setMeanRestTime] = useState(INITIAL_MEAN_REST_TIME);
    // Calculate theoretical load factor (rho)
    let rho = 0;
    const numServicePoints = servers + selfChecks;
    if (numServicePoints > 0 && meanArrivalInterval > 0 && meanServiceTime > 0) {
        const arrivalRateLambda = 1 / meanArrivalInterval;
        const serviceRateMuSys = numServicePoints * (1 / meanServiceTime);
        if (serviceRateMuSys > 0) {
            rho = arrivalRateLambda / serviceRateMuSys;
        }
    }
    const fetchData = useCallback(() => {
        setLoading(true);
        fetch('/api/simulations')
            .then((res) => res.json())
            .then((d) => {
            setData(d.sort((a, b) => new Date(b.simulationTimestamp || 0).getTime() - new Date(a.simulationTimestamp || 0).getTime()));
            setLoading(false);
        })
            .catch(err => {
            console.error("Failed to fetch simulations:", err);
            setError("Failed to load existing simulations.");
            setLoading(false);
        });
    }, []);
    useEffect(() => {
        fetchData();
    }, [fetchData]);
    const handleRunSimulation = () => {
        setLoading(true);
        setError(null);
        const params = new URLSearchParams({
            servers: servers.toString(),
            selfChecks: selfChecks.toString(),
            qmax: qmax.toString(),
            customers: numCustomers.toString(),
            meanArrivalInterval: meanArrivalInterval.toString(),
            meanServiceTime: meanServiceTime.toString(),
            meanRestTime: meanRestTime.toString(),
        });
        fetch(`/api/simulate?${params.toString()}`)
            .then(res => {
            if (!res.ok) {
                throw new Error(`Simulation request failed with status ${res.status}`);
            }
            return res.json();
        })
            .then((_newResult) => {
            fetchData();
        })
            .catch(err => {
            console.error("Failed to run simulation:", err);
            setError(err.message || "Failed to run simulation.");
            setLoading(false);
        });
    };
    const labels = data.map((sr) => sr.simulationTimestamp ? new Date(sr.simulationTimestamp).toLocaleTimeString() : `Run ${sr.id}`);
    const averages = data.map((d) => d.averageWaitTime ?? 0);
    const served = data.map((d) => d.customersServed ?? 0);
    const left = data.map((d) => d.customersLeft ?? 0);
    const chartData = {
        labels,
        datasets: [
            {
                label: 'Average Wait Time',
                data: averages,
                borderColor: 'rgb(75, 192, 192)',
                backgroundColor: 'rgba(75, 192, 192, 0.2)',
                yAxisID: 'y',
            },
            {
                label: 'Customers Served',
                data: served,
                borderColor: 'rgb(54, 162, 235)',
                backgroundColor: 'rgba(54, 162, 235, 0.2)',
                yAxisID: 'y1',
            },
            {
                label: 'Customers Left',
                data: left,
                borderColor: 'rgb(255, 99, 132)',
                backgroundColor: 'rgba(255, 99, 132, 0.2)',
                yAxisID: 'y1',
            },
        ],
    };
    const options = {
        responsive: true,
        interaction: {
            mode: 'index',
            intersect: false,
        },
        stacked: false,
        plugins: {
            legend: {
                position: 'top',
            },
            title: {
                display: true,
                text: 'Simulation Performance Metrics',
            },
        },
        scales: {
            y: {
                type: 'linear',
                display: true,
                position: 'left',
                title: {
                    display: true,
                    text: 'Avg. Wait Time (time units)'
                }
            },
            y1: {
                type: 'linear',
                display: true,
                position: 'right',
                title: {
                    display: true,
                    text: 'Number of Customers'
                },
                grid: {
                    drawOnChartArea: false,
                },
            },
        },
    };
    const handleResetParameters = useCallback(() => {
        setServers(INITIAL_SERVERS);
        setSelfChecks(INITIAL_SELF_CHECKS);
        setQmax(INITIAL_QMAX);
        setNumCustomers(INITIAL_NUM_CUSTOMERS);
        setMeanArrivalInterval(INITIAL_MEAN_ARRIVAL_INTERVAL);
        setMeanServiceTime(INITIAL_MEAN_SERVICE_TIME);
        setMeanRestTime(INITIAL_MEAN_REST_TIME);
        setError(null); // Also clear any existing error messages
    }, []); // useCallback to memoize the function if needed, though not strictly necessary here without complex dependencies
    return (_jsxs("div", { className: "App", children: [_jsx("h1", { children: "Discrete Event Simulator" }), _jsxs("div", { className: "simulation-controls", children: [_jsx("h2", { children: "Run New Simulation" }), _jsxs("div", { children: [_jsxs("label", { children: ["Servers: ", _jsx("input", { "data-testid": "servers-input", type: "number", value: servers, onChange: e => setServers(Math.max(1, parseInt(e.target.value))) })] }), _jsxs("label", { children: ["Self-Checkouts: ", _jsx("input", { type: "number", value: selfChecks, onChange: e => setSelfChecks(Math.max(0, parseInt(e.target.value))) })] })] }), _jsxs("div", { children: [_jsxs("label", { children: ["Max Queue Length (qmax): ", _jsx("input", { type: "number", value: qmax, onChange: e => setQmax(Math.max(1, parseInt(e.target.value))) })] }), _jsxs("label", { children: ["Customers: ", _jsx("input", { type: "number", value: numCustomers, onChange: e => setNumCustomers(Math.max(1, parseInt(e.target.value))) })] })] }), _jsxs("div", { children: [_jsxs("label", { children: ["Mean Inter-Arrival Time: ", _jsx("input", { type: "number", step: "0.1", value: meanArrivalInterval, onChange: e => setMeanArrivalInterval(Math.max(0.01, parseFloat(e.target.value))) })] }), _jsxs("label", { children: ["Mean Service Time: ", _jsx("input", { type: "number", step: "0.1", value: meanServiceTime, onChange: e => setMeanServiceTime(Math.max(0.01, parseFloat(e.target.value))) })] })] }), _jsx("div", { children: _jsxs("label", { children: ["Mean Rest Time (Servers): ", _jsx("input", { type: "number", step: "0.1", value: meanRestTime, onChange: e => setMeanRestTime(Math.max(0, parseFloat(e.target.value))) })] }) }), _jsxs("div", { className: "load-factor-display", children: [_jsxs("p", { children: ["Theoretical System Load (\u03C1): ", rho.toFixed(3), " (", Math.max(0, rho * 100).toFixed(1), "%)"] }), rho > 1 && _jsx("p", { style: { color: 'orange' }, children: "Warning: System is Overloaded (\u03C1 > 1)! Expect long queues/many lost customers." }), rho === 1 && _jsx("p", { style: { color: 'blue' }, children: "System is Critically Balanced (\u03C1 = 1)." }), rho < 0.3 && rho > 0 && _jsx("p", { style: { color: 'green' }, children: "System is Lightly Loaded (\u03C1 < 0.3)." }), meanRestTime > 0 && _jsx("p", { style: { color: 'purple', fontSize: '0.9em', marginTop: '5px' }, children: "Note: Theoretical load does not account for server rest times. Actual load may be higher." })] }), _jsxs("div", { className: "simulation-buttons", children: [_jsx("button", { "data-testid": "run-simulation", onClick: handleRunSimulation, disabled: loading, children: loading ? 'Running...' : 'Run Simulation' }), _jsx("button", { "data-testid": "reset-parameters", onClick: handleResetParameters, disabled: loading, className: "reset-button", children: "Reset Parameters" })] }), error && _jsxs("p", { className: "error-message", children: ["Error: ", error] })] }), _jsx("h2", { children: "Simulation Performance Metrics" }), loading && data.length === 0 && _jsx("p", { children: "Loading chart..." }), !loading && data.length === 0 && !error && _jsx("p", { children: "No simulation data available. Run a simulation to see results." }), data.length > 0 && _jsx(Line, { data: chartData, options: options }), _jsx("h2", { children: "Simulation History" }), loading && data.length === 0 && _jsx("p", { children: "Loading history..." }), _jsxs("table", { children: [_jsx("thead", { children: _jsxs("tr", { children: [_jsx("th", { children: "ID" }), _jsx("th", { children: "Timestamp" }), _jsx("th", { children: "Params (S, SC, Q, C)" }), _jsx("th", { children: "Avg. Wait" }), _jsx("th", { children: "Served" }), _jsx("th", { children: "Left" }), _jsx("th", { children: "Raw Result" })] }) }), _jsx("tbody", { children: data.map(sr => {
                            let rowClassName = "";
                            if (sr.customersLeft && sr.customersLeft > 0) {
                                // Highlight if any customers left, more strongly if many left
                                const leftPercentage = sr.customersLeft / ((sr.customersLeft ?? 0) + (sr.customersServed ?? 0));
                                if (leftPercentage > 0.5)
                                    rowClassName = "very-high-left"; // Over 50% left
                                else if (leftPercentage > 0.1)
                                    rowClassName = "high-left"; // Over 10% left
                                else
                                    rowClassName = "some-left"; // Any customers left
                            }
                            // Example: Highlight very high average wait times (e.g., > 5 time units, adjust as needed)
                            if (sr.averageWaitTime && sr.averageWaitTime > 5.0) {
                                rowClassName += " very-high-wait"; // Append class
                            }
                            return (_jsxs("tr", { className: rowClassName.trim(), children: [_jsx("td", { children: sr.id }), _jsx("td", { children: sr.simulationTimestamp ? new Date(sr.simulationTimestamp).toLocaleString() : 'N/A' }), _jsx("td", { children: `${sr.servers}, ${sr.selfChecks}, ${sr.qmax}, ${sr.customers}` }), _jsx("td", { children: sr.averageWaitTime?.toFixed(3) ?? 'N/A' }), _jsx("td", { children: sr.customersServed ?? 'N/A' }), _jsx("td", { children: sr.customersLeft ?? 'N/A' }), _jsx("td", { children: sr.result })] }, sr.id));
                        }) })] })] }));
}
export default App;
