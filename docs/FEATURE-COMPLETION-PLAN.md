# Script Module Completion Plan

Status: planning document for the standalone Script Editor + Teleprompter prototype (v0.6).
Scope: finish and validate the script-side experience before native Android/Windows integration. This plan does not implement recording, speech recognition, forced alignment, or waveform processing.

## Confirmed by manual testing

- [x] Short phrase can be selected in the teleprompter by clicking the first word and Shift-clicking the last word in the same section.
- [x] Selected phrase can be saved as a replacement request.
- [x] JSON export/import preserves the phrase selection and request data.
- [x] The JavaScript parses successfully.

## Remaining validation before integration

- [ ] Verify single-word replacement remains compatible with phrase replacement.
- [x] User confirmed named-draft saving works. [ ] Verify phrase replacement survives named-draft save/load and browser autosave recovery, not just JSON export/import.
- [x] Edit the source section after creating a request; narration edits mark requests stale, and the UI checks the stored text against the current range after import.
- [ ] Reorder sections and confirm requests still resolve by stable section ID rather than section position.
- [x] Deleting a section retains its markers and replacement requests and marks them orphaned in the feature panel.
- [x] Verify captions, transcript, production markers, separate cues, and replacement requests survive JSON export/import together (user-tested). [x] User confirmed named-draft saving works; mixed-feature named-draft reload still needs explicit confirmation.
- [ ] Manually validate WebVTT and SRT exports, including multiline text, punctuation, and timestamp formatting. Export now blocks malformed timestamps, non-positive cue durations, and empty caption text.
- [ ] Check keyboard accessibility, visible focus, narrow screens, and teleprompter controls.
- [ ] Run the complete workflow in a real browser; record the browser and test date in the release notes.

## Recommended implementation stages

### Stage A — Data integrity and UX hardening

1. [x] Add visible stale/orphaned status for replacement requests and orphaned markers.
2. [x] Caption text and timing edits trigger autosave; a separate draft-name field has been added, but named-draft persistence still needs manual verification.
3. Ensure draft save/load and autosave restore all feature data, including project settings and replacement request lifecycle fields.
4. Make import validation explicit: reject malformed project files with a useful error and do not partially overwrite the current project.
5. Add an in-app confirmation or recovery option before replacing an existing project on import.
6. Ensure selected phrase state is cleared or safely restored when the section text changes.

### Stage B — Transcript and captions

1. Keep transcript text distinct from the script; the transcript represents what was actually spoken.
2. Support editable caption cue start/end times and text.
3. [x] Provide a configurable maximum-word caption segmentation helper (3–30 words per cue); generated timings remain labelled as estimates until audio alignment exists.
4. [x] Validate timestamps and prevent end times earlier than or equal to start times during caption export.
5. Export valid WebVTT and SRT; test special characters, multiline text, empty cues, and timestamp edge cases.
6. Later, attach alignment provenance (manual, estimated, or engine-aligned) to each cue.

### Stage C — Production markers and cues

1. Keep section production cues (instructions shown to the reader/producer) distinct from production markers (events anchored to a section or script range).
2. Define a stable marker schema: ID, type, note, section ID, optional inclusive start/end word indices, and optional future media/time anchor.
3. Support marker types such as intro, outro, pause, emphasis, music, sound effect, retake, pickup, and editor note.
4. Clearly label script-anchored markers as not yet audio-timed.
5. Ensure section reorder preserves anchors by ID and section deletion produces an explicit orphan state.

### Stage D — Phrase replacement handoff

1. Keep the request non-destructive and store the original selected text, inclusive start/end word indices, nearby context, and a stable section ID.
2. Store a source-text fingerprint or revision identifier so the audio side can detect when the selected text has changed.
3. Use a lifecycle such as pending, recorded, ready_for_review, approved, cancelled, and stale. Do not imply recording or blending has happened in this browser prototype.
4. On the audio side, resolve text anchors using transcript alignment and actual media metadata; never convert word indices directly into sample offsets.
5. Record a new take without modifying the original; retain links to source take, replacement take, and edit operation.
6. Preview boundary-aware edits with short fades/crossfades and context before approval. Do not automatically approve a blend solely because the algorithm produced output.
7. Test timing, room tone, breaths, coarticulation, plosives, loudness, pitch, and pacing; provide undo and A/B comparison.

## Integration contract principles

- Script module owns text, section IDs/order, cues, transcript, caption text/timing, marker intent, and replacement intent.
- Audio module owns media files, immutable original takes, sample/time coordinates, recording, alignment, waveform edits, fades, and rendered audio.
- Word indices are zero-based text anchors only. They are not timecodes or sample offsets.
- Use stable IDs rather than visible section positions.
- Keep JSON versioned and add migrations when the schema changes.
- Preserve old single-word requests: a legacy `wordIndex` is the start index, and `startWordIndex`/`endWordIndex` are inclusive bounds.

## Release gate

Do not call the script module integration-ready until the manual checklist above passes in a browser and the integration contract is reviewed alongside the native audio engine API. The current prototype is not yet an audio recorder or waveform editor.
