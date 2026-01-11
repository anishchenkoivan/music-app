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

  // Fetch user's artist profiles on mount
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
        // User might not have an artist profile yet
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

    // Validate file
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
      
      // Success - navigate to home
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
      <h2>Upload Track</h2>

      {step === 1 && (
        <form onSubmit={handleMetadataSubmit}>
          <div className="form-group">
            <label htmlFor="title">Track Title</label>
            <input
              id="title"
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              required
              disabled={loading}
            />
          </div>

          <div className="form-group">
            <label htmlFor="artist">Artist ID</label>
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
                <p className="info" style={{fontSize: '0.9em', color: '#666', marginBottom: '0.5rem'}}>
                  No artist profile found. Enter artist ID manually:
                </p>
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

          {error && <div className="error">{error}</div>}

          <button type="submit" disabled={loading || !artistId}>
            {loading ? 'Creating...' : 'Next'}
          </button>
        </form>
      )}

      {step === 2 && (
        <div className="file-upload">
          <div className="form-group">
            <label htmlFor="file">Select MP3 File</label>
            <input
              id="file"
              type="file"
              accept=".mp3,audio/mpeg"
              onChange={handleFileChange}
              disabled={loading}
            />
          </div>

          {file && (
            <div className="file-info">
              <p>Selected: {file.name}</p>
              <p>Size: {(file.size / 1024 / 1024).toFixed(2)} MB</p>
            </div>
          )}

          {uploadProgress > 0 && (
            <div className="progress">
              <div className="progress-bar">
                <div 
                  className="progress-fill" 
                  style={{ width: `${uploadProgress}%` }}
                />
              </div>
              <span>{uploadProgress}%</span>
            </div>
          )}

          {error && <div className="error">{error}</div>}

          <button 
            onClick={handleFileUpload} 
            disabled={!file || loading}
          >
            {loading ? 'Uploading...' : 'Upload'}
          </button>
        </div>
      )}
    </div>
  );
}
