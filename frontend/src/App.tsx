import { useEffect, useState } from 'react';
import { Line } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';
import './App.css';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
);

interface SimulationResult {
  id: number;
  servers: number;
  selfChecks: number;
  qmax: number;
  customers: number;
  result: string;
  averageWaitTime?: number;
  customersServed?: number;
  customersLeft?: number;
  simulationTimestamp?: string;
}

function App() {
  const [data, setData] = useState<SimulationResult[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [servers, setServers] = useState(1);
  const [selfChecks, setSelfChecks] = useState(0);
  const [qmax, setQmax] = useState(1);
  const [numCustomers, setNumCustomers] = useState(10);
  const [meanArrivalInterval, setMeanArrivalInterval] = useState(0.5);
  const [meanServiceTime, setMeanServiceTime] = useState(1.0);

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

  const fetchData = () => {
    setLoading(true);
    fetch('/simulations')
      .then((res) => res.json())
      .then((d: SimulationResult[]) => {
        setData(d.sort((a, b) => new Date(b.simulationTimestamp || 0).getTime() - new Date(a.simulationTimestamp || 0).getTime()));
        setLoading(false);
      })
      .catch(err => {
        console.error("Failed to fetch simulations:", err);
        setError("Failed to load existing simulations.");
        setLoading(false);
      });
  };

  useEffect(() => {
    fetchData();
  }, []);

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
    });

    fetch(`/simulate?${params.toString()}`)
      .then(res => {
        if (!res.ok) {
          throw new Error(`Simulation request failed with status ${res.status}`);
        }
        return res.json();
      })
      .then((newResult: SimulationResult) => {
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
      mode: 'index' as const,
      intersect: false,
    },
    stacked: false,
    plugins: {
      legend: {
        position: 'top' as const,
      },
      title: {
        display: true,
        text: 'Simulation Performance Metrics',
      },
    },
    scales: {
      y: {
        type: 'linear' as const,
        display: true,
        position: 'left' as const,
        title: {
            display: true,
            text: 'Avg. Wait Time (time units)'
        }
      },
      y1: {
        type: 'linear' as const,
        display: true,
        position: 'right' as const,
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

  return (
    <div className="App">
      <h1>Discrete Event Simulator</h1>

      <div className="simulation-controls">
        <h2>Run New Simulation</h2>
        <div>
          <label>Servers: <input type="number" value={servers} onChange={e => setServers(Math.max(1, parseInt(e.target.value)))} /></label>
          <label>Self-Checkouts: <input type="number" value={selfChecks} onChange={e => setSelfChecks(Math.max(0, parseInt(e.target.value)))} /></label>
        </div>
        <div>
          <label>Max Queue Length (qmax): <input type="number" value={qmax} onChange={e => setQmax(Math.max(1, parseInt(e.target.value)))} /></label>
          <label>Customers: <input type="number" value={numCustomers} onChange={e => setNumCustomers(Math.max(1, parseInt(e.target.value)))} /></label>
        </div>
        <div>
          <label>Mean Inter-Arrival Time: <input type="number" step="0.1" value={meanArrivalInterval} onChange={e => setMeanArrivalInterval(Math.max(0.01, parseFloat(e.target.value)))} /></label>
          <label>Mean Service Time: <input type="number" step="0.1" value={meanServiceTime} onChange={e => setMeanServiceTime(Math.max(0.01, parseFloat(e.target.value)))} /></label>
        </div>
        <div className="load-factor-display">
          <p>Theoretical System Load (ρ): {rho.toFixed(3)} ({Math.max(0, rho * 100).toFixed(1)}%)</p>
          {rho > 1 && <p style={{ color: 'orange' }}>Warning: System is Overloaded (ρ &gt; 1)! Expect long queues/many lost customers.</p>}
          {rho === 1 && <p style={{ color: 'blue' }}>System is Critically Balanced (ρ = 1).</p>}
          {rho < 0.3 && rho > 0 && <p style={{ color: 'green' }}>System is Lightly Loaded (ρ &lt; 0.3).</p>}
        </div>
        <button onClick={handleRunSimulation} disabled={loading}>
          {loading ? 'Running...' : 'Run Simulation'}
        </button>
        {error && <p className="error-message">Error: {error}</p>}
      </div>

      <h2>Simulation Performance Metrics</h2>
      {loading && data.length === 0 && <p>Loading chart...</p>}
      {!loading && data.length === 0 && !error && <p>No simulation data available. Run a simulation to see results.</p>}
      {data.length > 0 && <Line data={chartData} options={options} />}

      <h2>Simulation History</h2>
      {loading && data.length === 0 && <p>Loading history...</p>}
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Timestamp</th>
            <th>Params (S, SC, Q, C)</th>
            <th>Avg. Wait</th>
            <th>Served</th>
            <th>Left</th>
            <th>Raw Result</th>
          </tr>
        </thead>
        <tbody>
          {data.map(sr => {
            let rowClassName = "";
            if (sr.customersLeft && sr.customersLeft > 0) {
              // Highlight if any customers left, more strongly if many left
              const leftPercentage = sr.customersLeft / ( (sr.customersLeft ?? 0) + (sr.customersServed ?? 0));
              if (leftPercentage > 0.5) rowClassName = "very-high-left"; // Over 50% left
              else if (leftPercentage > 0.1) rowClassName = "high-left"; // Over 10% left
              else rowClassName = "some-left"; // Any customers left
            }
            // Example: Highlight very high average wait times (e.g., > 5 time units, adjust as needed)
            if (sr.averageWaitTime && sr.averageWaitTime > 5.0) {
              rowClassName += " very-high-wait"; // Append class
            }

            return (
              <tr key={sr.id} className={rowClassName.trim()}>
                <td>{sr.id}</td>
                <td>{sr.simulationTimestamp ? new Date(sr.simulationTimestamp).toLocaleString() : 'N/A'}</td>
                <td>{`${sr.servers}, ${sr.selfChecks}, ${sr.qmax}, ${sr.customers}`}</td>
                <td>{sr.averageWaitTime?.toFixed(3) ?? 'N/A'}</td>
                <td>{sr.customersServed ?? 'N/A'}</td>
                <td>{sr.customersLeft ?? 'N/A'}</td>
                <td>{sr.result}</td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}

export default App;
