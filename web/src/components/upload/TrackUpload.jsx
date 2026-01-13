import { useState, useEffect } from 'react';
import { musicAPI } from '../../api/music.js';
import { useNavigate } from 'react-router-dom';

export default function TrackUpload() {
  const [step, setStep] = useState(1);
  const [title, setTitle] = useState('');
  const [artistId, setArtistId] = useState('');
  const [userArtists, setUserArtists] = useState([]);
  const [trackId, setTrackId] = useState(null);
  const [uploadToken, setUploadToken] = useState(null);
  const [file, setFile] = useState(null);
  const [uploadProgress, setUploadProgress] = useState(0);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  const navigate = useNavigate();

  useEffect(() => {
    const fetchUserArtists = async () => {
      try {
        const userId = localStorage.getItem('user_id');
        if (userId) {
          const artists = await musicAPI.getUserArtists(userId);
          setUserArtists(Array.isArray(artists) ? artists : [artists]);
          if (artists && (Array.isArray(artists) ? artists.length > 0 : true)) {
            setArtistId(Array.isArray(artists) ? artists[0].id : artists.id);
          }
        }
      } catch (err) {
        console.error('Error fetching artists:', err);
      }
    };
    fetchUserArtists();
  }, []);

  const handleMetadataSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      if (!artistId) {
        setError('Please enter an artist ID. You need to create an artist profile first.');
        setLoading(false);
        return;
      }
      
      const response = await musicAPI.uploadTrackMetadata(title, [artistId]);
      
      setTrackId(response.trackId);
      setUploadToken(response.uploadToken);
      setStep(2);
    } catch (err) {
      setError('Failed to create track metadata: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    if (!selectedFile) return;

    if (!selectedFile.name.endsWith('.mp3')) {
      setError('Only MP3 files are allowed');
      return;
    }

    const maxSize = 100 * 1024 * 1024; // 100MB
    const minSize = 100 * 1024; // 100KB
    
    if (selectedFile.size > maxSize) {
      setError('File size must be less than 100MB');
      return;
    }

    if (selectedFile.size < minSize) {
      setError('File size must be at least 100KB');
      return;
    }

    setFile(selectedFile);
    setError('');
  };

  const handleFileUpload = async () => {
    if (!file) {
      setError('Please select a file');
      return;
    }

    setError('');
    setLoading(true);

    try {
      await musicAPI.uploadAudioFile(
        trackId,
        file,
        uploadToken,
        setUploadProgress
      );
      
      alert('Track uploaded successfully!');
      navigate('/');
    } catch (err) {
      setError('Failed to upload file: ' + (err.response?.data?.message || err.message));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="track-upload">
      <h1>📤 Upload Track</h1>
      
      {/* Progress Indicator */}
      <div style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        gap: '1rem',
        marginBottom: '2rem',
        padding: '1rem',
        background: 'var(--bg-secondary)',
        borderRadius: 'var(--border-radius)',
      }}>
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: '0.5rem',
          color: step >= 1 ? 'var(--accent-primary)' : 'var(--text-tertiary)',
          fontWeight: step === 1 ? '700' : '500'
        }}>
          <span style={{
            width: '30px',
            height: '30px',
            borderRadius: '50%',
            background: step >= 1 ? 'var(--accent-gradient)' : 'var(--bg-tertiary)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: 'white',
            fontWeight: 'bold'
          }}>1</span>
          <span>Metadata</span>
        </div>
        <div style={{
          width: '50px',
          height: '2px',
          background: step >= 2 ? 'var(--accent-primary)' : 'var(--border-color)'
        }}></div>
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: '0.5rem',
          color: step >= 2 ? 'var(--accent-primary)' : 'var(--text-tertiary)',
          fontWeight: step === 2 ? '700' : '500'
        }}>
          <span style={{
            width: '30px',
            height: '30px',
            borderRadius: '50%',
            background: step >= 2 ? 'var(--accent-gradient)' : 'var(--bg-tertiary)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: 'white',
            fontWeight: 'bold'
          }}>2</span>
          <span>Upload File</span>
        </div>
      </div>

      {step === 1 && (
        <div className="card">
          <div className="card-header">
            <h2 className="card-title">🎵 Track Information</h2>
            <p style={{ color: 'var(--text-secondary)', marginTop: '0.5rem' }}>
              Enter the basic details about your track
            </p>
          </div>
          <form onSubmit={handleMetadataSubmit}>
            <div className="form-group">
              <label htmlFor="title">🎼 Track Title</label>
              <input
                id="title"
                type="text"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder="Enter your track title"
                required
                disabled={loading}
              />
            </div>

            <div className="form-group">
              <label htmlFor="artist">👤 Artist</label>
              {userArtists.length > 0 ? (
                <select
                  id="artist"
                  value={artistId}
                  onChange={(e) => setArtistId(e.target.value)}
                  required
                  disabled={loading}
                >
                  {userArtists.map(artist => (
                    <option key={artist.id} value={artist.id}>
                      {artist.name}
                    </option>
                  ))}
                </select>
              ) : (
                <div>
                  <div className="info" style={{ marginBottom: '0.75rem' }}>
                    ℹ️ No artist profile found. Enter artist ID manually
                  </div>
                  <input
                    id="artist"
                    type="text"
                    value={artistId}
                    onChange={(e) => setArtistId(e.target.value)}
                    placeholder="Enter artist UUID"
                    required
                    disabled={loading}
                  />
                </div>
              )}
            </div>

            {error && <div className="error">❌ {error}</div>}

            <button type="submit" disabled={loading || !artistId}>
              {loading ? (
                <>
                  <span className="loading"></span> Creating...
                </>
              ) : (
                '➡️ Next Step'
              )}
            </button>
          </form>
        </div>
      )}

      {step === 2 && (
        <div className="card">
          <div className="card-header">
            <h2 className="card-title">📁 Upload Audio File</h2>
            <p style={{ color: 'var(--text-secondary)', marginTop: '0.5rem' }}>
              Select your MP3 file (100KB - 100MB)
            </p>
          </div>
          <div className="file-upload">
            <div className="form-group">
              <label htmlFor="file">🎵 Select MP3 File</label>
              <input
                id="file"
                type="file"
                accept=".mp3,audio/mpeg"
                onChange={handleFileChange}
                disabled={loading}
                style={{
                  padding: '1rem',
                  border: '2px dashed var(--border-color)',
                  borderRadius: 'var(--border-radius)',
                  cursor: 'pointer',
                  transition: 'all var(--transition-speed) ease'
                }}
              />
            </div>

            {file && (
              <div className="file-info">
                <h4 style={{ marginBottom: '0.75rem', color: 'var(--text-primary)' }}>
                  ✅ File Selected
                </h4>
                <p>📄 <strong>Name:</strong> {file.name}</p>
                <p>📊 <strong>Size:</strong> {(file.size / 1024 / 1024).toFixed(2)} MB</p>
              </div>
            )}

            {uploadProgress > 0 && (
              <div className="progress">
                <div className="progress-bar" style={{
                  height: '12px',
                  background: 'var(--bg-tertiary)',
                  borderRadius: 'var(--border-radius-sm)',
                  overflow: 'hidden'
                }}>
                  <div
                    className="progress-fill"
                    style={{
                      width: `${uploadProgress}%`,
                      background: 'var(--accent-gradient)',
                      height: '100%',
                      transition: 'width 0.3s ease'
                    }}
                  />
                </div>
                <span style={{
                  display: 'block',
                  textAlign: 'center',
                  marginTop: '0.75rem',
                  fontSize: '1.1rem',
                  fontWeight: '700',
                  color: 'var(--accent-primary)'
                }}>
                  📤 Uploading... {uploadProgress}%
                </span>
              </div>
            )}

            {error && <div className="error">❌ {error}</div>}

            <div style={{ display: 'flex', gap: '1rem', marginTop: '1.5rem' }}>
              <button
                type="button"
                className="button-secondary"
                onClick={() => {
                  setStep(1);
                  setFile(null);
                  setUploadProgress(0);
                  setError('');
                }}
                disabled={loading}
                style={{ flex: 1 }}
              >
                ⬅️ Back
              </button>
              <button
                onClick={handleFileUpload}
                disabled={!file || loading}
                style={{ flex: 2 }}
              >
                {loading ? (
                  <>
                    <span className="loading"></span> Uploading...
                  </>
                ) : (
                  '🚀 Upload Track'
                )}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
