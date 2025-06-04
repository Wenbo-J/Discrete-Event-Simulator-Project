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
}

function parseResult(result: string) {
  const m = result.match(/\[(?<avg>[\d.]+) (?<served>\d+) (?<left>\d+)\]/);
  if (!m || !m.groups) return { avg: 0, served: 0, left: 0 };
  return {
    avg: parseFloat(m.groups.avg),
    served: parseInt(m.groups.served, 10),
    left: parseInt(m.groups.left, 10),
  };
}

function App() {
  const [data, setData] = useState<SimulationResult[]>([]);

  useEffect(() => {
    fetch('/simulations')
      .then((res) => res.json())
      .then((d) => setData(d));
  }, []);

  const labels = data.map((_, i) => `Run ${i + 1}`);
  const averages = data.map((d) => parseResult(d.result).avg);

  const chartData = {
    labels,
    datasets: [
      {
        label: 'Average Wait Time',
        data: averages,
        borderColor: 'rgb(75, 192, 192)',
        backgroundColor: 'rgba(75, 192, 192, 0.2)',
      },
    ],
  };

  const options = {
    responsive: true,
    plugins: {
      legend: {
        position: 'top' as const,
      },
      title: {
        display: true,
        text: 'Simulation Performance',
      },
    },
  };

  return (
    <div className="App">
      <h1>Simulation Metrics</h1>
      <Line data={chartData} options={options} />
    </div>
  );
}

export default App;
