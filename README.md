# Audio Studio — Script Editor + Teleprompter (Prototype v0.1)

A standalone browser prototype to validate the first Creator Tools workflow before native Android and Windows implementation.

## Try it online

Open [index.html](./index.html) in this repository. To run it locally, download `index.html` and open it in a modern browser (Firefox, Chrome, Edge, Safari). No package manager or server is required after download.

## Included
- Script title and editor
- Word and character count, plus estimated reading duration
- Configurable speaking pace, teleprompter font size, line spacing, and scroll speed
- Full-screen teleprompter with start/pause/reset
- Keyboard controls while open: `Space` play/pause, `Up`/`Down` adjust speed, `Esc` exit
- Save, load, and delete drafts using browser local storage
- Export script as plain text

## Important prototype notes
- Drafts are stored locally in the current browser profile; they are not synced to GitHub or other devices.
- Browser privacy settings or local-file restrictions can affect storage. Use Export .txt as a backup.
- No microphone, recording, transcription, or native app integration is included yet.

## Planned integration
After usability feedback, implement native versions in Android (Kotlin) and Windows (WinUI 3/C#), with a shared script data format and autosave/recovery. Keep platform UI and scrolling separate from Audio Studio's shared C++ audio-processing core unless shared logic is actually needed.
