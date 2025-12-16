import React, { useState } from 'react';
import './App.css';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

function App() {
  const [inputText, setInputText] = useState('');
  const [polishedText, setPolishedText] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleRefine = async () => {
    if (!inputText.trim()) {
      setError('Please enter some text to refine');
      return;
    }

    setLoading(true);
    setError('');
    setPolishedText('');

    try {
      const response = await fetch(`${API_URL}/refine`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ text: inputText }),
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      setPolishedText(data.polishedText);
    } catch (err) {
      setError('Failed to refine text. Please try again.');
      console.error('Error:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="App">
      <header className="App-header">
        <h1>Tone Polish</h1>
        <p>Transform your messages to be professional, empathetic, and concise</p>
      </header>
      
      <main className="App-main">
        <div className="input-section">
          <label htmlFor="input-text">Your Draft Message:</label>
          <textarea
            id="input-text"
            className="text-area"
            value={inputText}
            onChange={(e) => setInputText(e.target.value)}
            placeholder="Enter your rough draft here..."
            rows={8}
          />
          <button
            className="refine-button"
            onClick={handleRefine}
            disabled={loading || !inputText.trim()}
          >
            {loading ? 'Refining...' : 'Refine'}
          </button>
        </div>

        <div className="result-section">
          <label htmlFor="result-text">Polished Version:</label>
          <textarea
            id="result-text"
            className="text-area result-text"
            value={polishedText}
            readOnly
            placeholder={loading ? 'Refining your message...' : 'Your polished message will appear here'}
            rows={8}
          />
        </div>

        {error && (
          <div className="error-message">
            {error}
          </div>
        )}
      </main>
    </div>
  );
}

export default App;

