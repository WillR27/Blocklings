package com.willr27.blocklings.entity.blockling.skill;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.willr27.blocklings.entity.blockling.skill.info.SkillInfo;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;

public class SkillsConfig
{
    public static final Path CONFIG_DIR = FMLPaths.CONFIGDIR.get().toAbsolutePath();

    private static final Gson GSON = new Gson();

    public static void read(String filename)
    {
        try
        {
            File file = new File(CONFIG_DIR.toString(), filename);
            JsonReader reader = new JsonReader(new FileReader(file));
            SkillInfo[] infos = GSON.fromJson(reader, SkillInfo[].class);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}
