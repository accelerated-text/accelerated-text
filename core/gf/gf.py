import six
import logging
import subprocess

try:
    from tempfile import TemporaryDirectory
except ImportError:
    from backports.tempfile import TemporaryDirectory


logger = logging.getLogger("gf")


try:
    import pgf
except ImportError:
    raise RuntimeError("Failed to import module 'pgf'. It's GrammaticalFramework runtime library which needs to be compiled and installed")


if six.PY2:
    # FileNotFoundError is only available since Python 3.3
    FileNotFoundError = IOError
    from io import open


class GFError(RuntimeError):
    pass


def compile_grammar(name, content):
    with TemporaryDirectory() as tmpdir:
        logger.debug("Created temp dir: {}".format(tmpdir))
        files = ["{0}/{1}.gf".format(tmpdir, k)
                 for k in content.keys()
                 if k != name]
        for k, v in content.items():
            with open("{0}/{1}.gf".format(tmpdir, k), "w", encoding="UTF-8") as f:
                try:
                    f.write(v.decode('utf-8'))
                except UnicodeEncodeError:
                    f.write(v)

        logger.info("Compiling")
        cmd = "gf -i /opt/gf/lang-utils/ -i /opt/gf/concept-net/ --output-dir={path} -make {files} {main}".format(
                path=tmpdir,
                main="{0}/{1}.gf".format(tmpdir, name),
                files=" ".join(files)
        )
        logger.debug("Compile command: {}".format(cmd))
        proc = subprocess.Popen(
            cmd,
            shell=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE
        )
        (result, error) = proc.communicate()

        if proc.returncode != 0:
            logger.error(error)
            return None, error
        else:
            logger.debug("Compiled successfuly! Message: {}".format(result))
            grammar = pgf.readPGF("{0}/{1}.pgf".format(tmpdir, name))
            logger.debug("Languages: {}".format(grammar.languages))
            return grammar, None


def serialize_bracket(bracket):
    if isinstance(bracket, str):
        return bracket

    return {
        "cat": bracket.cat,
        "fid": bracket.fid,
        "fun": bracket.fun,
        "children": list([serialize_bracket(c) for c in bracket.children])
    }


def generate_variants(expressions, concrete_grammar):
    return [{"tree": map(serialize_bracket, bracket)}
            for (_, e) in expressions
            for bracket in concrete_grammar.bracketedLinearizeAll(e)]


def generate_expressions(abstract_grammar):
    start_cat = abstract_grammar.startCat
    logger.debug("Start category: {}".format(start_cat))
    expressions = list(abstract_grammar.generateAll(start_cat))
    logger.debug("Expressions: {}".format(expressions))
    return expressions


def generate_results(name, content):
    (grammar, error) = compile_grammar(name, content)
    logger.debug("Grammar: {}".format(grammar))
    if grammar:
        logger.info("Generating")
        expressions = generate_expressions(grammar)
        return [(k, generate_variants(expressions, concrete))
                for k, concrete in grammar.languages.items()]
    else:
        raise GFError(error)


def parse_text(name, content, text):
    grammar = pgf.readPGF("/grammars/LangEng.pgf")
    logger.debug("Grammar: {}".format(grammar))
    logger.info("Parsing")
    return [(k, [str(e) for p, e in concrete.parse(text)])
            for k, concrete in grammar.languages.items()]
