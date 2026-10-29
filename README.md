# Audio Studio — Script Editor + Teleprompter (Prototype v0.2)

A standalone browser prototype to validate the Creator Tools workflow before native Android and Windows implementation.

## Try it locally

Download `index.html` and open it in a modern browser (Firefox, Chrome, Edge, Safari). No package manager or server is required after download.

## Included

- Script title and editor
- Live word count, character count, and estimated read duration
- Configurable speaking pace, teleprompter font size, line spacing, and scroll speed
- Full-screen teleprompter with start/pause/reset and keyboard controls: `Space`, `Up`/`Down`, `Esc`
- Named script drafts: save, load, and delete
- Automatic local recovery snapshot while editing
- Starter templates for video, podcast, tutorial, and product promo scripts
- Insertable production markers for intro, main point, pause, and outro
- Export the current script as plain text

## Storage and privacy

Drafts and the recovery snapshot are stored in the current browser profile using local storage. They are not synced to GitHub or other devices. Browser privacy settings and local-file restrictions can affect storage. Use **Save draft** and **Export .txt** for extra copies.

## Current limitations

This is a workflow prototype only. It does not connect to a microphone, record audio, transcribe speech, or integrate with the native Android/Windows applications. Production markers are plain text labels; they do not yet drive timeline actions.

## Next development steps

1. Test autosave recovery, templates, and markers in your target browser.
2. Add editable named sections and reorderable script blocks if the workflow feels right.
3. Define a platform-neutral script data model before native Kotlin and WinUI 3 implementations.
4. Keep platform UI and teleprompter scrolling separate from Audio Studio's shared C++ audio-processing core unless shared logic is actually needed.
