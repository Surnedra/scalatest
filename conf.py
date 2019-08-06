import argparse, re, os
from jinja2 import Environment, FileSystemLoader
#OTHRR - SUrnedra
CONF_PATTERN = "APPLICATION_CONF_"
DEFAULT_CONF_TEMPLATE_FILE = "application.conf.template"
DEFAULT_CONF_FILE = "application.conf"

'''
This script builds the application.conf file for Spark job. The application.conf file should be a Jinja2 template.
The template variables should be preset in environment variables and appended with CONF_PATTERN ('APPLICATION_CONF_' by default).
Example:
For a template file, application.conf.template:

cos {
    endpoint: {{ END_POINT }}
    username: {{ USER_NAME }}
}

APPLICATION_CONF_END_POINT and APPLICATION_CONF_USER_NAME should be set in the environment.

Script parameters
    -p: absolute path the template, optional
    -f: template file name, optional    
'''

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('-p', dest='path', help='Absolute path to the application conf file template (e.g. /build_dir/src/main/resources)', default=None)
    parser.add_argument('-f', dest='file', help='Template file name', default=None)
    args = parser.parse_args()

    if (not args.path) and ("JENKINS_BUILD_DIR" not in os.environ):
        raise SystemExit("No application.conf.template file found! Please provide the path to the file.")

    file_path = args.path if args.path else os.path.join(os.environ["JENKINS_BUILD_DIR"], "src/main/resources")
    file_name = args.file if args.file else DEFAULT_CONF_TEMPLATE_FILE
    pattern = "(?<={})\w+".format(CONF_PATTERN)
    config_keys = [k for k in os.environ if re.search(pattern, k)]
    context = {}

    print("Application conf template context:")
    for k in config_keys:
        key = re.findall(pattern, k)[0]
        context[key] = os.environ[k]
        print("{key}:{val}".format(key=key, val=("*" * len(context[key]))))

    env = Environment(loader=FileSystemLoader(file_path))
    template = env.get_template(name=file_name)
    properties = template.render(**context)
    print("Saving application conf file to {}".format(os.path.join(file_path, DEFAULT_CONF_FILE)))
    with open(os.path.join(file_path, DEFAULT_CONF_FILE), 'w') as f:
        f.write(properties)


