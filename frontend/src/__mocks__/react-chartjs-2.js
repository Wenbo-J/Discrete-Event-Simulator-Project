import { jsxs as _jsxs, jsx as _jsx } from "react/jsx-runtime";
// Mock the Line component from react-chartjs-2
export const Line = ({ data, options: _options }) => {
    return (_jsxs("div", { "data-testid": "chart-line", children: [_jsxs("div", { children: ["Chart: ", data?.datasets?.length || 0, " datasets"] }), _jsxs("div", { children: ["Labels: ", data?.labels?.join(', ') || 'none'] })] }));
};
// Mock other chart components if needed
export const Bar = ({ data: _data, options: _options }) => {
    return _jsx("div", { "data-testid": "chart-bar", children: "Bar Chart Mock" });
};
export const Pie = ({ data: _data, options: _options }) => {
    return _jsx("div", { "data-testid": "chart-pie", children: "Pie Chart Mock" });
};
