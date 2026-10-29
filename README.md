# Audio Studio — Script Editor + Teleprompter (Standalone Prototype v0.4)

A local-first browser prototype for validating the Creator Tools workflow before native Android and Windows integration.

## Try it locally

1. Open or download [`index.html`](./index.html).
2. Open it in a modern browser (Firefox, Chrome, Edge, or Safari).
3. No build step, server, account, or network connection is required.

## Version 0.4 features

- **Editable script sections:** give each section a title, narration, and optional production cue.
- **Reorder sections:** move sections up or down; teleprompter navigation can jump directly to a section or move to the previous/next section.
- **Cue visibility:** separate production cues are excluded from narration by default, with a teleprompter toggle to show or hide them during rehearsal/production mode.
- **Cue filtering:** filter the editor list to all sections, sections with cues, or sections without cues.
- **Inline markers:** insert plain-text markers such as `[INTRO]`, `[MAIN POINT]`, `[PAUSE]`, and `[OUTRO]`. These remain in narration.
- **Multiple exports:** export narration-only text, a production copy with section headings and cue notes, or a JSON project backup containing section/cue data and reading settings.
- **Import:** import project JSON to restore sections, cues, and reading settings; import plain text and split it into sections.
- **Local draft management:** save, load, and delete named drafts; autosave/recovery includes sections and cues.
- **Starter templates:** video intro, podcast episode, tutorial, and product promo templates.
- **Reading tools:** word/character counts, estimated duration, speaking pace, text size, line spacing, preview, and full-screen scrolling teleprompter.
- **Keyboard controls:** Space toggles scrolling, Up/Down adjusts speed, and Escape exits the teleprompter.

## Data and privacy

Named drafts and recovery snapshots are stored in the current browser profile using `localStorage`. They do not sync between devices. Export a JSON project file for portable backup or transfer. Clearing browser storage may remove local drafts.

## Current limitations

This is still a standalone prototype. It does not connect to a microphone, record audio, transcribe speech, or integrate with the native Audio Studio application. JSON is the structured project backup format; text exports are intended for reading and production handoff.

## Next stage

Test the complete section workflow in the browser before designing native Android and Windows implementations. Use the validated section/cue model as the functional reference for native work.

## Project

[Audio Studio Modules repository](https://github.com/mosescryptic/Audio-Studio-Modules)
