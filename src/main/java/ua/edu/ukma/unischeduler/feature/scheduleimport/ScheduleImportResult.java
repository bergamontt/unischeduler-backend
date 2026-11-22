package ua.edu.ukma.unischeduler.feature.scheduleimport;

public record ScheduleImportResult(
        Long scopeId,
        int specialitiesCreated,
        int coursesCreated,
        int classesCreated,
        int classesUpdated,
        int classesUnchanged,
        int classesArchived,
        int classesDeleted
) {}
