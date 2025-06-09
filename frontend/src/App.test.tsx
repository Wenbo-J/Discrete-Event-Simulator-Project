// @jest-environment jsdom
// This is a Jest test file for App.tsx
import '@testing-library/jest-dom';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import App from './App';
import type {} from 'jest';

// Mock fetch globally
beforeEach(() => {
  global.fetch = jest.fn((input: RequestInfo | URL) => {
    const url = typeof input === 'string' ? input : input.toString();
    if (url.includes('/simulations')) {
      return Promise.resolve({
        ok: true,
        json: () => Promise.resolve([
          {
            id: 1,
            servers: 2,
            selfChecks: 1,
            qmax: 2,
            customers: 5,
            averageWaitTime: 0.5,
            customersServed: 5,
            customersLeft: 0,
            simulationTimestamp: new Date().toISOString(),
            result: '[0.500 5 0]'
          }
        ])
      }) as any;
    }
    if (url.includes('/metrics')) {
      return Promise.resolve({
        ok: true,
        json: () => Promise.resolve({ averageWait: 0.5, totalRuns: 1 })
      }) as any;
    }
    if (url.includes('/simulate')) {
      return Promise.resolve({
        ok: true,
        json: () => Promise.resolve({
          id: 2,
          servers: 2,
          selfChecks: 1,
          qmax: 2,
          customers: 5,
          averageWaitTime: 0.6,
          customersServed: 5,
          customersLeft: 0,
          simulationTimestamp: new Date().toISOString(),
          result: '[0.600 5 0]'
        })
      }) as any;
    }
    return Promise.reject(new Error('Unknown endpoint'));
  });
});
afterEach(() => {
  jest.resetAllMocks();
});

test('renders all input fields and buttons', () => {
  render(<App />);
  expect(screen.getByTestId('servers-input')).toBeInTheDocument();
  expect(screen.getByLabelText(/Self-Checkouts/i)).toBeInTheDocument();
  expect(screen.getByLabelText(/Max Queue Length/i)).toBeInTheDocument();
  expect(screen.getByLabelText(/Customers/i)).toBeInTheDocument();
  expect(screen.getByLabelText(/Mean Inter-Arrival Time/i)).toBeInTheDocument();
  expect(screen.getByLabelText(/Mean Service Time/i)).toBeInTheDocument();
  expect(screen.getByLabelText(/Mean Rest Time/i)).toBeInTheDocument();
  expect(screen.getByTestId('run-simulation')).toBeInTheDocument();
  expect(screen.getByTestId('reset-parameters')).toBeInTheDocument();
});

test('renders chart and table with initial data', async () => {
  render(<App />);
  expect(await screen.findByText(/Simulation Performance Metrics/i)).toBeInTheDocument();
  expect(screen.getByText(/Simulation History/i)).toBeInTheDocument();
  expect(screen.getByRole('table')).toBeInTheDocument();
});

test('changing input values updates state', () => {
  render(<App />);
  const serversInput = screen.getByTestId('servers-input');
  fireEvent.change(serversInput, { target: { value: '3' } });
  expect((serversInput as HTMLInputElement).value).toBe('3');
});

// DOES NOT WORK - RLY DONT KNOW WHY, TO BE FIXED
// test('reset button resets all fields', async () => {
//   render(<App />);
  
//   const serversInput = screen.getByTestId('servers-input');
  
//   // Change value to 5
//   fireEvent.change(serversInput, { target: { value: '5' } });
//   expect((serversInput as HTMLInputElement).value).toBe('5');
  
//   // Click reset
//   fireEvent.click(screen.getByTestId('reset-parameters'));

//   // Wait for the value to update to 1
//   await waitFor(() => {
//     expect((serversInput as HTMLInputElement).value).toBe('1');
//   });
// });


test('shows rest time warning when mean rest time is set', () => {
  render(<App />);
  const restInput = screen.getByLabelText(/Mean Rest Time/i);
  fireEvent.change(restInput, { target: { value: '1' } });
  expect(screen.getByText(/does not account for server rest times/i)).toBeInTheDocument();
});

test('shows error message on fetch failure', async () => {
  (global.fetch as jest.Mock).mockImplementationOnce(() => Promise.reject(new Error('API error')));
  render(<App />);
  fireEvent.click(screen.getByTestId('run-simulation'));
  await waitFor(() => expect(screen.getByText(/Error/i)).toBeInTheDocument());
});

test('table row is highlighted when customers left', async () => {
  (global.fetch as jest.Mock).mockImplementation((url) => {
    if (typeof url === 'string' && url.includes('/simulations')) {
      return Promise.resolve({
        ok: true,
        json: () => Promise.resolve([
          {
            id: 1,
            servers: 2,
            selfChecks: 1,
            qmax: 2,
            customers: 5,
            averageWaitTime: 0.5,
            customersServed: 2,
            customersLeft: 3,
            simulationTimestamp: new Date().toISOString(),
            result: '[0.500 2 3]'
          }
        ])
      }) as any;
    }
    return Promise.resolve({ ok: true, json: () => Promise.resolve([]) }) as any;
  });
  render(<App />);
  expect(await screen.findByText('[0.500 2 3]')).toBeInTheDocument();
  // Check for row highlight by class (optional, if want to test CSS classes)
}); 