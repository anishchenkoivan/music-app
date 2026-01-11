import { useState } from 'react';
import { useAuthStore } from '../../store/authStore.js';
import { useNavigate } from 'react-router-dom';

export default function RegisterForm() {
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    displayName: '',
    bio: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  const register = useAuthStore(state => state.register);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    // Validation
    if (formData.username.length < 3 || formData.username.length > 50) {
      setError('Username must be 3-50 characters');
      setLoading(false);
      return;
    }

    if (formData.password.length < 8) {
      setError('Password must be at least 8 characters');
      setLoading(false);
      return;
    }

    try {
      const success = await register(formData);
      if (success) {
        navigate('/');
      } else {
        setError('Registration failed. Username or email may already exist.');
      }
    } catch (err) {
      setError('Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="card auth-card">
        <div className="card-header">
          <h2 className="card-title">✨ Create Account</h2>
          <p style={{ color: 'var(--text-secondary)', marginTop: '0.5rem' }}>
            Join us and start your music journey
          </p>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="username">👤 Username</label>
            <input
              id="username"
              name="username"
              type="text"
              value={formData.username}
              onChange={handleChange}
              placeholder="Choose a unique username"
              required
              disabled={loading}
              minLength={3}
              maxLength={50}
            />
            <small style={{ color: 'var(--text-tertiary)', fontSize: '0.85rem' }}>
              3-50 characters
            </small>
          </div>

          <div className="form-group">
            <label htmlFor="email">📧 Email</label>
            <input
              id="email"
              name="email"
              type="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="your.email@example.com"
              required
              disabled={loading}
            />
          </div>
          
          <div className="form-group">
            <label htmlFor="password">🔒 Password</label>
            <input
              id="password"
              name="password"
              type="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="Create a strong password"
              required
              disabled={loading}
              minLength={8}
            />
            <small style={{ color: 'var(--text-tertiary)', fontSize: '0.85rem' }}>
              Minimum 8 characters
            </small>
          </div>

          <div className="form-group">
            <label htmlFor="displayName">✏️ Display Name (optional)</label>
            <input
              id="displayName"
              name="displayName"
              type="text"
              value={formData.displayName}
              onChange={handleChange}
              placeholder="How should we call you?"
              disabled={loading}
              maxLength={100}
            />
          </div>

          <div className="form-group">
            <label htmlFor="bio">📝 Bio (optional)</label>
            <textarea
              id="bio"
              name="bio"
              value={formData.bio}
              onChange={handleChange}
              placeholder="Tell us about yourself..."
              disabled={loading}
              maxLength={500}
              rows={3}
            />
          </div>

          {error && <div className="error">❌ {error}</div>}

          <button type="submit" disabled={loading}>
            {loading ? (
              <>
                <span className="loading"></span> Creating account...
              </>
            ) : (
              '🎉 Create Account'
            )}
          </button>
        </form>
      </div>
    </div>
  );
}
