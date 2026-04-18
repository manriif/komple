package komple.project

import komple.project.c.KompleCProjectOptions

/**
 * C project.
 */
public abstract class KompleCProject internal constructor(projectName: String) :
    KompleProject(projectName),
    KompleCProjectOptions {

    }