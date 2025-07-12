import React from 'react';

// Mock the Line component from react-chartjs-2
export const Line = ({ data, options }: any) => {
  return (
    <div data-testid="chart-line">
      <div>Chart: {data?.datasets?.length || 0} datasets</div>
      <div>Labels: {data?.labels?.join(', ') || 'none'}</div>
    </div>
  );
};

// Mock other chart components if needed
export const Bar = ({ data: _data, options: _options }: any) => {
  return <div data-testid="chart-bar">Bar Chart Mock</div>;
};

export const Pie = ({ data: _data, options: _options }: any) => {
  return <div data-testid="chart-pie">Pie Chart Mock</div>;
}; 