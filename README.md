# Audio Studio — Script Editor + Teleprompter (Prototype v0.3)

A standalone, local-first browser prototype for validating the Creator Tools workflow before native Android and Windows integration.

## Try it locally

1. Open or download [`index.html`](./index.html).
2. Open it in a modern browser (Firefox, Chrome, Edge, or Safari).
3. No build step, server, account, or network connection is required.

## Version 0.3 features

- **Editable script sections:** keep intro, main content, transitions, outro, or any custom section separate.
- **Reorder sections:** use the up/down controls to change the narration order.
- **Separate production cues:** add pause, music, SFX, emphasis, and recording notes in a cue field attached to each section. Cue text is excluded from the clean teleprompter narration.
- **Inline narration markers:** insert plain-text markers such as `[INTRO]`, `[MAIN POINT]`, `[PAUSE]`, and `[OUTRO]`. These remain in the narration and exports.
- **Two export formats:** export a narration-only `.txt` file, or a production copy that includes section headings and separate cue notes.
- **Local draft management:** save, load, and delete named drafts; autosave/recovery includes sections and cues.
- **Starter templates:** video intro, podcast episode, tutorial, and product promo templates are expanded into editable sections.
- **Reading tools:** word/character counts, estimated duration, speaking pace, text size, line spacing, preview, and full-screen scrolling teleprompter.
- **Keyboard controls:** Space toggles scrolling, Up/Down adjusts speed, and Escape exits the teleprompter.

## Data and privacy

Drafts and recovery snapshots are stored in the current browser profile using `localStorage`. They do not sync between devices. Export files for backups or transfer. Clearing browser storage may remove local drafts.

## Current limitations

This remains a standalone prototype. It does not connect to a microphone, record audio, transcribe speech, or integrate with the native Audio Studio application. Inline markers are plain text; separate production cues are structured by section but are not yet connected to recording or editing actions.

## Roadmap

- **v0.4:** evaluate cue filtering, section-level teleprompter navigation, and import/export format improvements.
- **Later integration:** adapt the validated section/cue model to Audio Studio's native project architecture without coupling this browser prototype to the audio engine.

## Project

[Audio Studio Modules repository](https://github.com/mosescryptic/Audio-Studio-Modules)
