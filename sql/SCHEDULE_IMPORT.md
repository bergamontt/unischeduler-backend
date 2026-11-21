# Schedule importer backend extension

POST endpoint `/api/admin/schedule-imports/replace`.
The endpoint is disabled by default and requires a separate shared secret.
The changes are compatible with the original CRUD APIs and do not delete
historical user-created classes.

## How to run it

1. Set `UNISCHEDULER_IMPORT_ENABLED=true` and the `UNISCHEDULER_IMPORT_TOKEN` in the backend `.env`.
   `docker-compose.yaml` passes both values into the service container.
2. Start the backend as specified in the README.md (Java 25 / Docker).
3. Back up PostgreSQL, review `2026_schedule_import.sql` and execute it once.
4. Install/use the separate Python importer and use `import --dry-run` first.
5. Send authenticated requests with header `X-Import-Token`.

## Scope and replacement semantics

A scope is `(speciality_id, study_year, semester, academic_year)`.
Each incoming occurrence has a stable `sourceKey`. Existing classes with the
same sourceKey are updated **in place** (preserving class ID and overrides);
unchanged classes retain their existing records. Absent imported classes are
removed when they have no overrides; classes referenced by user
`ClassOverride` are marked `archived`, and the calendar query excludes them.
If a previously archived sourceKey returns, the same record is unarchived.

All changes for a request happen inside one Spring transaction. If a request
fails, the database transaction rolls back. Imported classes are tagged by
ImportScope; manually entered classes and imports from other scopes are not
removed. This is an authoritative replacement only of **import-owned**
entries for the selected scope, not a cleanup of all historical manual data.

## Endpoint payload

```json
{
  "specialityName": "Інженерія програмного забезпечення",
  "faculty": "інформатики",
  "department": "<supply real department>",
  "academicYear": "2026-2027",
  "studyYear": 4,
  "semester": "AUTUMN",
  "occurrences": [
    {
      "sourceKey": "<64-character deterministic SHA-256 hex>",
      "courseName": "Проектування програмних систем",
      "teacher": "доц. А.М. Нагірна",
      "group": "2",
      "location": "1-108",
      "description": null,
      "startsAt": "2026-09-07T10:00:00",
      "endsAt": "2026-09-07T11:20:00",
      "sourceRow": 13,
      "sourceMetadata": "{... original source fields ...}"
    }
  ]
}
```

Teacher, group, location and description may be null on imported classes;
source strings are preserved in `sourceMetadata` TEXT. Unlike PersonalEvent,
these entries are common university events, not events owned by a user.

## Important limits

- Course.department does not appear in Excel. The CLI requires it explicitly.
- Course.year is treated as *study year*; ImportScope separately tracks the
  academic year (2026-2027).
- `CourseClass.description` now appears in the common CalendarEvent projection.
