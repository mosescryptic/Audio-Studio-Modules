# Script Module ↔ Audio/Waveform Integration Contract

Status: design contract for the standalone Script Editor + Teleprompter prototype (v0.6). This document is intentionally scoped to the **script/transcript/caption/marker side**. It does not implement audio recording or waveform editing.

## Goals

- Keep script authoring useful without an audio device, account, network, or native application.
- Preserve stable references from words and sections to future audio-side operations.
- Keep original recordings immutable; audio-side replacement should create a new take/edit operation, never silently overwrite the source.
- Keep platform-independent script metadata separate from platform-specific audio buffers and waveform UI.
- Make project JSON forward-compatible and versioned.

## Ownership boundary

### Script module owns

- Section order, titles, narration, and separate production cue text.
- Transcript text and caption cue text/timing fields.
- Production marker intent and its script anchor.
- User's selected word and replacement request/context.
- Reading/teleprompter preferences and JSON/text interchange.

### Audio/waveform module owns

- Media files, immutable original takes, take versions, sample rate/channel layout, sample/frame coordinates, and media duration.
- Recording device setup, recording, waveform rendering, playback, trim/split/move operations, undo/redo, and audio render.
- Transcript/word alignment against actual media.
- Converting a script anchor into an audio time/sample range after alignment.
- Replacement recording, boundary selection, fades/crossfades, loudness/tone matching, and audition/approval.
- Any destructive-looking operation must be represented non-destructively until the user explicitly commits an export/render.

The script module must **not** invent audio timestamps from reading pace or assume that a word index equals a sample/time position.

## Current project JSON shape

The app exports a versioned JSON object similar to this:

```json
{
  "format": "audio-studio-script-project",
  "version": 2,
  "title": "Example narration",
  "sections": [
    {
      "id": "section-1",
      "title": "Introduction",
      "text": "Welcome to the show.",
      "cue": "Leave a short pause before the next line."
    }
  ],
  "transcript": {
    "text": "Welcome to the show."
  },
  "captions": [
    {
      "id": "caption-...",
      "start": "00:00:00.000",
      "end": "00:00:02.000",
      "text": "Welcome to the show.",
      "timingSource": "estimated-draft"
    }
  ],
  "productionMarkers": [
    {
      "id": "marker-...",
      "type": "pause",
      "note": "Short pause",
      "sectionId": "section-1",
      "anchor": "script-section",
      "status": "script-anchored",
      "createdAt": "ISO-8601 timestamp"

    }
  ],
  "replacementRequests": [
    {
      "id": "replacement-...",
      "sectionId": "section-1",
      "wordIndex": 1,
      "startWordIndex": 1,
      "endWordIndex": 1,
      "word": "to",
      "selectedText": "to",
      "before": "Welcome",
      "after": "the show.",
      "status": "pending",
      "strategy": "replace-phrase-audio-with-context-crossfade"
    }
  ],
  "settings": {
    "pace": 140,
    "font": 42,
    "speed": 30,
    "spacing": 1.6
  },
  "updated": "ISO-8601 timestamp"
}
```

This is an illustrative contract, not a guarantee that every optional property exists in every imported legacy file. Importers should tolerate missing optional fields and older v1 projects. Preserve unknown fields where practical when adding future versions.

### Stable anchors

- `sections[].id` is the stable section identity. Keep it when editing/reordering a section; generate a new ID only when creating a new section.
- `wordIndex` remains the legacy zero-based start index for compatibility. `startWordIndex` and `endWordIndex` define an inclusive zero-based token range within the current section text; for a single-word request they are equal. These are script references, not audio coordinates.
- Word indices can become stale when narration changes. The prototype compares the selected text and saved nearby context with the current section text, and flags mismatches as stale. The audio side must independently validate the anchor against the current transcript/alignment before resolving it; stale anchors require user review/reselection.
- `productionMarkers[].anchor` is `script-section` or `script-word`; word markers include `wordIndex`, `anchorText`, and nearby `contextBefore`/`contextAfter` when created by the current prototype. `status` starts as `script-anchored`; the UI displays `stale` if the current token/context no longer matches and `orphaned` if the section no longer exists. Legacy word markers without context remain readable, but only fields present in the record can be verified.
- Captions use start/end time strings in the current prototype, but their `timingSource` must be checked. `estimated-draft` is not aligned audio timing.

## Word / short-phrase re-recording workflow

The prototype supports selecting a single word or a short phrase by clicking the first word and Shift-clicking the last word in the same section. It creates a pending request only; the intended future workflow is:

1. **Select and anchor.** User clicks a word, then Shift-clicks the last word of the target phrase in the same section. Store section ID, inclusive start/end word indices, selected text, and nearby context. Keep `wordIndex` as an alias for the start index for legacy consumers.
2. **Resolve against media.** Audio side finds the matching transcript alignment and maps the word to a time/sample interval. If there is no trusted alignment, request a manual range selection instead of guessing.
3. **Capture context.** Play a short lead-in and tail; record the replacement phrase with enough context to match the speaker's timing, room tone, breath, and vocal delivery. Prefer replacing a complete short phrase when coarticulation makes a single-word cut sound unnatural.
4. **Choose safe boundaries.** Do not cut exactly at the linguistic word boundary by default. Find low-energy/appropriate phonetic boundaries; preserve plosives, fricatives, breaths, and coarticulation.
5. **Preview a non-destructive edit.** Create a new take/edit operation with source range, replacement range, fades, gain adjustment, and optional time-stretch settings. Keep the original take untouched.
6. **Blend and audition.** Use short, adaptive crossfades only where they sound natural. A crossfade cannot fix mismatched pitch, room acoustics, timing, or a word whose phonemes overlap neighboring words. If needed, record a longer phrase rather than a single word.
7. **Approve and render.** Let the user A/B the edit, undo it, or commit it to a new rendered version. Keep provenance back to the original take and replacement recording.

### Suggested future audio-side operation (conceptual)

```json
{
  "operation": "replace-audio-range",
  "requestId": "replacement-...",
  "sourceMediaId": "media-id-owned-by-audio-module",
  "sourceRange": { "startSample": 123456, "endSample": 156789 },
  "replacementTakeId": "take-id-owned-by-audio-module",
  "boundaryFades": { "inMs": 12, "outMs": 18 },
  "timeStretch": { "enabled": false },
  "gainDb": 0,
  "status": "preview"
}
```

This operation is deliberately not emitted by the script prototype: only the audio module knows sample rate, media identity, and real aligned boundaries.

## Transcript and captions

- Transcript text should be editable independently from the planned narration, because speakers may improvise or make mistakes.
- A future transcription service may propose text, but the user should be able to correct it.
- Forced alignment takes a known transcript plus actual audio and estimates time boundaries; it is distinct from transcription. Alignment results should record their source/confidence and be re-runnable after transcript edits.
- Caption export should only be treated as production-ready after timings are aligned/reviewed. The prototype's estimated caption times are placeholders for UI workflow testing.
- Keep internal caption cues format-neutral. Export SRT/WebVTT at the boundary rather than using either format as the in-memory project model.

## Production marker types

The prototype supports pause, emphasis, music, sound effect, retake, chapter/segment, caption break, intro, and outro. More types can be added without assigning audio timestamps at authoring time. In future, add optional fields for marker status, color/category, and resolved media anchor, but keep script anchor and audio anchor as distinct concepts.

## Integration rules for Android and Windows

1. Use the same logical project schema in Kotlin and C#; do not make browser DOM structures part of the data contract.
2. Keep shared script/transcript/marker rules platform-independent. Keep recording and DSP in the existing native audio-engine boundary.
3. Treat imports as untrusted data: validate project shape/version, unique section IDs, metadata array shapes, caption timestamp syntax/duration, and references before applying. Reject invalid imports without mutating the current project. Empty caption text may be preserved as an unfinished draft cue, but export must reject it until completed.
4. Preserve stable section IDs during edits and migrations.
5. Use explicit project schema migrations instead of silently changing the meaning of existing fields.
6. Store media separately from script metadata. A script project can reference media IDs owned by the audio project, but must not embed large audio data in JSON.
7. Keep operations undoable and non-destructive; never replace original takes in place.
8. Provide a clear status for each replacement request: pending, needs-alignment, ready-to-record, recorded, preview, approved, rejected, or stale.

## Validation checklist before native integration

- [ ] JSON export → import preserves section IDs, cues, transcript, captions, markers, replacement requests, and reading settings; invalid JSON/project structures are rejected without changing the current project.
- [ ] Reordering sections does not change their IDs.
- [ ] Editing text that invalidates a selected word or its nearby context causes the request/marker to be revalidated or marked stale.
- [ ] SRT/WebVTT exports contain real line breaks and valid time syntax.
- [ ] Estimated caption timings are visibly identified as draft.
- [ ] Selecting a word or phrase creates only an intent/anchor; no audio is changed.
- [ ] The native audio module resolves inclusive start/end anchors only against trusted alignment/media data.
- [ ] Replacement previews preserve the original take and support undo/A-B comparison.
