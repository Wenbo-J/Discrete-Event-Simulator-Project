module.exports = {
  preset: 'ts-jest',
  testEnvironment: 'jsdom',
  setupFilesAfterEnv: ['@testing-library/jest-dom'],
  moduleNameMapper: {
    '\\.(css|less|scss|sass)$': 'identity-obj-proxy', // Mock CSS imports
    '^.+\\.svg$': 'jest-transformer-svg', // If you use SVGs
  },
  transform: {
    '^.+\\.tsx?$': ['ts-jest', { /* ts-jest config options here */ }],
  },
}; 