import { render, screen } from '@testing-library/react';
import App from './App';

test('renders simulator title', () => {
  render(<App />);
  const titleElement = screen.getByText(/Discrete Event Simulator/i);
  expect(titleElement).toBeInTheDocument();
}); 