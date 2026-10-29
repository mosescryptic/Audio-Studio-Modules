# Audio Studio — Script Editor + Teleprompter (Standalone Prototype v0.5)

A local-first browser prototype for validating the Creator Tools workflow before native Android and Windows integration.

## Try it locally

1. Open or download [`index.html`](./index.html).
2. Open it in a modern browser (Firefox, Chrome, Edge, or Safari).
3. No build step, server, account, or network connection is required.

## Version 0.5 features

- **Editable script sections:** each section has a title, narration, and optional separate production cue.
- **Stable section IDs:** IDs are preserved in project JSON, autosave, and named drafts so markers and replacement requests can refer to a durable script anchor across reloads.
- **Reorder and navigate:** move sections up/down, jump to a section, and navigate previous/next in the teleprompter.
- **Cue visibility and filtering:** show/hide separate production cues in the teleprompter and filter sections by whether cues exist.
- **Inline production markers:** insert markers such as `[INTRO]`, `[MAIN POINT]`, `[PAUSE]`, and `[OUTRO]` directly into narration when they should be read or remain visible in the script.
- **Structured production markers:** add section-anchored or word-anchored markers for pause, emphasis, music, sound effects, retakes, chapters, caption breaks, intro, and outro. These are script anchors, not audio timestamps.
- **Transcript workspace:** maintain a transcript independently from the planned script; copy the script into it as a starting point and edit it to match what was actually spoken.
- **Caption drafts:** generate editable caption cues from transcript sentences, manually adjust each cue's start/end times and text, and export SubRip (`.srt`) or WebVTT (`.vtt`).
- **Word-level selection:** click a word in the teleprompter to highlight it and anchor a production marker or create a word-replacement request with surrounding context.
- **Replacement intent export:** JSON projects preserve the selected word, section ID, word index, neighboring words, and intended replacement strategy for later waveform-side implementation.
- **Multiple project exports:** narration-only `.txt`, production copy `.txt`, and structured project `.json`.
- **Import:** import JSON project data or plain text split into sections. JSON imports preserve section IDs, transcript, captions, markers, replacement requests, and reading settings.
- **Local draft management:** save/load/delete named drafts; autosave/recovery includes the expanded metadata.
- **Starter templates and reading tools:** video intro, podcast, tutorial, product promo; word/character counts, estimated reading duration, pace, text size, line spacing, preview, and full-screen scrolling.
- **Keyboard controls:** Space toggles scrolling, Up/Down adjusts speed, and Escape exits the teleprompter.

## Captions and transcript limitations

Caption generation currently makes **estimated draft timings** from transcript length. It does not listen to audio, perform speech recognition, or align words to a recording. Check and correct all cue timings against the final audio before publishing. WebVTT is the preferred interoperable caption format for web video; SRT is provided for common editing workflows.

## Word-level re-recording: current scope

The teleprompter can highlight a selected word and save a replacement request containing:
- Stable section ID and zero-based word index
- Selected word and a small amount of left/right text context
- A pending status and a proposed replacement strategy

It does **not** record audio, locate samples, cut a waveform, time-stretch, crossfade, or blend a replacement take. Those responsibilities belong to the future audio/waveform module. See [the integration contract](./docs/SCRIPT-MODULE-INTEGRATION.md) before connecting the modules.

## Data and privacy

Named drafts and recovery snapshots are stored in the current browser profile using `localStorage`. They do not sync between devices. Export a JSON project file for portable backup or transfer. Clearing browser storage may remove local drafts. The module does not send script or transcript data to a server.

## Current limitations

- No microphone access, audio recording, speech recognition, diarization, or forced alignment.
- No audio waveform, destructive edits, audio render, crossfade, or take replacement.
- Caption timings are estimated/manual until aligned with a recording.
- No collaboration or cross-device sync.
- This is a browser workflow prototype, not yet the native Android or Windows UI.

## Recommended integration order

1. Validate section editing, save/load, JSON round-trip, cue visibility, transcript/caption editing, and word selection in the browser.
2. Adopt the documented JSON data contract in the native script module.
3. Let the audio engine own media, sample/time coordinates, take versions, waveform edits, and non-destructive rendering.
4. Add transcript-to-audio alignment, then use the stable script/word anchors to resolve replacement requests into real audio ranges.
5. Only after alignment and recording are implemented, build replacement recording and boundary-aware audio blending.

## Project

[Audio Studio Modules repository](https://github.com/mosescryptic/Audio-Studio-Modules)
