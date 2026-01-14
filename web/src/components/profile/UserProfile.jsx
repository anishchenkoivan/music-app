import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { userAPI } from '../../api/user.js';
import { useAuthStore } from '../../store/authStore.js';

export default function UserProfile() {
  const { id } = useParams();
  const navigate = useNavigate();
  const currentUserId = useAuthStore(state => state.userId);
  const [userData, setUserData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [isEditing, setIsEditing] = useState(false);
  const [editForm, setEditForm] = useState({
    firstName: '',
    lastName: '',
    username: '',
    bio: '',
    country: '',
    email: '',
    profilePicture: ''
  });

  const isOwnProfile = currentUserId === id;

  useEffect(() => {
    fetchUserProfile();
  }, [id]);

  const fetchUserProfile = async () => {
    try {
      setLoading(true);
      setError('');
      const data = await userAPI.getUser(id);
      setUserData(data);
      setEditForm({
        firstName: data.firstName || '',
        lastName: data.lastName || '',
        username: data.username || '',
        bio: data.bio || '',
        country: data.country || '',
        email: data.email || '',
        profilePicture: data.profilePicture || ''
      });
    } catch (err) {
      setError('Failed to load user profile: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  const handleEditSubmit = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      setError('');
      await userAPI.updateUser(id, editForm);
      await fetchUserProfile();
      setIsEditing(false);
      alert('Profile updated successfully!');
    } catch (err) {
      setError('Failed to update profile: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setEditForm(prev => ({ ...prev, [name]: value }));
  };

  if (loading && !userData) {
    return (
      <div className="user-profile" style={{ textAlign: 'center', padding: '3rem' }}>
        <div className="loading" style={{ fontSize: '3rem' }}>⏳</div>
        <p style={{ marginTop: '1rem', color: 'var(--text-secondary)' }}>Loading profile...</p>
      </div>
    );
  }

  if (error && !userData) {
    return (
      <div className="user-profile" style={{ textAlign: 'center', padding: '3rem' }}>
        <div className="error">❌ {error}</div>
        <button onClick={() => navigate('/')} style={{ marginTop: '1rem' }}>
          🏠 Back to Home
        </button>
      </div>
    );
  }

  return (
    <div className="user-profile">
      <div className="card">
        <div className="card-header">
          <h1 className="card-title">👤 User Profile</h1>
          {isOwnProfile && !isEditing && (
            <button
              onClick={() => setIsEditing(true)}
              style={{
                padding: '0.5rem 1rem',
                fontSize: '0.9rem'
              }}
            >
              ✏️ Edit Profile
            </button>
          )}
          {isEditing && (
            <button
              onClick={() => {
                setIsEditing(false);
                setEditForm({
                  firstName: userData.firstName || '',
                  lastName: userData.lastName || '',
                  username: userData.username || '',
                  bio: userData.bio || '',
                  country: userData.country || '',
                  email: userData.email || '',
                  profilePicture: userData.profilePicture || ''
                });
              }}
              className="button-secondary"
              style={{
                padding: '0.5rem 1rem',
                fontSize: '0.9rem'
              }}
            >
              ❌ Cancel
            </button>
          )}
        </div>

        {!isEditing ? (
          <div className="profile-view">
            {userData.profilePicture && (
              <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
                <img
                  src={userData.profilePicture}
                  alt={`${userData.username}'s profile`}
                  style={{
                    width: '150px',
                    height: '150px',
                    borderRadius: '50%',
                    objectFit: 'cover',
                    border: '4px solid var(--accent-primary)'
                  }}
                />
              </div>
            )}

            <div className="profile-info" style={{
              display: 'grid',
              gridTemplateColumns: '150px 1fr',
              gap: '1rem',
              marginBottom: '1rem'
            }}>
              <strong>👤 Username:</strong>
              <span>{userData.username}</span>

              <strong>✉️ Email:</strong>
              <span>{userData.email}</span>

              <strong>📝 First Name:</strong>
              <span>{userData.firstName}</span>

              <strong>📝 Last Name:</strong>
              <span>{userData.lastName}</span>

              <strong>🌍 Country:</strong>
              <span>{userData.country}</span>

              <strong>💬 Bio:</strong>
              <span style={{ whiteSpace: 'pre-wrap' }}>{userData.bio || 'No bio provided'}</span>
            </div>
          </div>
        ) : (
          <form onSubmit={handleEditSubmit}>
            <div className="form-group">
              <label htmlFor="username">👤 Username</label>
              <input
                id="username"
                name="username"
                type="text"
                value={editForm.username}
                onChange={handleInputChange}
                required
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <label htmlFor="email">✉️ Email</label>
              <input
                id="email"
                name="email"
                type="email"
                value={editForm.email}
                onChange={handleInputChange}
                required
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <label htmlFor="firstName">📝 First Name</label>
              <input
                id="firstName"
                name="firstName"
                type="text"
                value={editForm.firstName}
                onChange={handleInputChange}
                required
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <label htmlFor="lastName">📝 Last Name</label>
              <input
                id="lastName"
                name="lastName"
                type="text"
                value={editForm.lastName}
                onChange={handleInputChange}
                required
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <label htmlFor="country">🌍 Country</label>
              <input
                id="country"
                name="country"
                type="text"
                value={editForm.country}
                onChange={handleInputChange}
                required
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <label htmlFor="bio">💬 Bio</label>
              <textarea
                id="bio"
                name="bio"
                value={editForm.bio}
                onChange={handleInputChange}
                rows="4"
                disabled={loading}
                placeholder="Tell us about yourself..."
              />
            </div>

            <div className="form-group">
              <label htmlFor="profilePicture">🖼️ Profile Picture URL</label>
              <input
                id="profilePicture"
                name="profilePicture"
                type="url"
                value={editForm.profilePicture}
                onChange={handleInputChange}
                disabled={loading}
                placeholder="https://example.com/your-image.jpg"
              />
            </div>

            {error && <div className="error">❌ {error}</div>}

            <div style={{ display: 'flex', gap: '1rem', marginTop: '1.5rem' }}>
              <button
                type="submit"
                disabled={loading}
                style={{ flex: 2 }}
              >
                {loading ? (
                  <>
                    <span className="loading"></span> Saving...
                  </>
                ) : (
                  '💾 Save Changes'
                )}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}
