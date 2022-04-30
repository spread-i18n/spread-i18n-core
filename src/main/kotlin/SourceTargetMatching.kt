@Suppress("ClassName")
class iOSSourceTargetMatcher: SourceTargetMatcher {

    private fun matchesStrongly(source: SourceColumn, target: TargetDirectory): Boolean {
        val sourceTag = source.title.normalizedTag
        val targetTag = target.file.nameWithoutExtension.normalizedTag
        return sourceTag == targetTag
    }

    private fun matchesWeakly(source: SourceColumn, target: TargetDirectory): Boolean {
        val name = target.file.nameWithoutExtension.normalizedTag
        val locale = source.locales.find { locale -> locale.toLanguageTag().normalizedTag == name }
        return locale != null
    }

    private val TargetDirectory.isBaseDir: Boolean
        get() = this.file.name.toLowerCase().endsWith("base.lproj")


    override fun match(sources: Collection<SourceColumn>, targets: Collection<TargetDirectory>): MatchedSourcesAndTargets {
        val matchedSourcesAndTargets = MatchedSourcesAndTargets()
        for (source in sources) {
            for (target in targets) {
                if (matchesStrongly(source, target)) {
                    matchedSourcesAndTargets.add(MatchedSourceAndTarget(source, target))
                    break
                }
            }
        }

        var remainingSources = sources.filter { source -> matchedSourcesAndTargets.notContainsSource(source) }
        var remainingTargets = targets.filter { target -> matchedSourcesAndTargets.notContainsTarget(target) }

        for (source in remainingSources) {
            for (target in remainingTargets) {
                if (matchesWeakly(source, target)) {
                    matchedSourcesAndTargets.add(MatchedSourceAndTarget(source, target))
                    break
                }
            }
        }

        remainingSources = sources.filter { source -> matchedSourcesAndTargets.notContainsSource(source) }
        remainingTargets = targets.filter { target -> matchedSourcesAndTargets.notContainsTarget(target) }

        if ((remainingSources.size==1) && (remainingTargets.size==1) && remainingTargets[0].isBaseDir) {
            matchedSourcesAndTargets.add(MatchedSourceAndTarget(remainingSources[0], remainingTargets[0]))
        }
        return matchedSourcesAndTargets
    }
}

