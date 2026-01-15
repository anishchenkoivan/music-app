import { useThemeStore } from '../../store/themeStore.js';

export default function ThemeToggle() {
  const { theme, toggleTheme } = useThemeStore();
  
  return (
    <button
      onClick={toggleTheme}
      className="theme-toggle"
      aria-label="Toggle theme"
      title={`Switch to ${theme === 'light' ? 'dark' : 'light'} mode`}
    >
      {theme === 'light' ? '🌙' : '☀️'}
    </button>
  );
}
