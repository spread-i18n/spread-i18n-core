import java.io.BufferedWriter
import java.nio.file.Files
import java.nio.file.Path


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

@Suppress("ClassName")
class iOSFileWriter(private val targetDirectoryPath: Path): TranslationFileWriter {

    private val filePath: Path by lazy {
        targetDirectoryPath.resolve("Localizable.strings")
    }

    private val writer: BufferedWriter by lazy {
        Files.newBufferedWriter(filePath)
    }

    init {
        Files.newBufferedReader(filePath).use { reader ->
            reader.lineSequence().withIndex().find { it.value.contains("*/") }?.let {
                copyHeaderToNewFile(endPositionOfHeader = it.index)
            }
        }
    }

    private fun copyHeaderToNewFile(endPositionOfHeader: Int) {
        try {
            Files.newBufferedReader(filePath).use { reader ->
                reader.lineSequence().withIndex().take(endPositionOfHeader+1).forEach { line ->
                    writer.write(line.value+"\n")
                }
            }
        } catch (e: Exception) {
            val foo = 1
            val bar = foo
        }
    }

    override fun write(key: String, value: String) {
        if (key.startsWith("//")) {
            writer.write("$key\n")
        } else if (key.isNotBlank()) {
            writer.write("$key = \"$value\";\n")
        }
        println("$key = $value")
    }

    override fun close() {
        writer.flush()
        writer.close()
    }

}
