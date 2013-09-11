package com.mojang.minecraft.render;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;

public class Shader
{
  private int vertex_shader = 0;
  private int fragment_shader = 0;
  private int program = 0;
  private FloatBuffer matrix = BufferUtils.createFloatBuffer(16);

  private boolean disable_error_msg = true;
  private boolean debug = false;

  private ArrayList<Integer> value_ids = new ArrayList<Integer>();

  private boolean pre_opengl20 = false;

  @SuppressWarnings("resource")
Shader(String path)
  {
    String vert_path = path + ".vert";
    String frag_path = path + ".frag";

    String line = "";
    String vertex_shader_source = "";

    URI uri = null;
    try {
      uri = new URI("file://" + Shader.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    } catch (URISyntaxException e1) {
      throw new RuntimeException("URI fault");
    }

    File f = new File(uri);
    BufferedReader reader;
    try
    {
      reader = new BufferedReader(new FileReader(f.getParent() + vert_path));
      while ((line = reader.readLine()) != null)
        vertex_shader_source = vertex_shader_source + line + "\n";
    }
    catch (Exception e)
    {
      throw new RuntimeException("Couldnt load shader! Was looking for: " + f.getParent() + vert_path + " \nReason: " + e);
    }

    String version = GL11.glGetString(7938);

    if (Integer.valueOf(version.substring(0, 1)).intValue() < 2)
    {
      this.pre_opengl20 = true;
      if (!GLContext.getCapabilities().GL_ARB_shader_objects)
        throw new RuntimeException("Pre OpenGL 2.0 without GL_ARB_shader_objects support.");
      if (!GLContext.getCapabilities().GL_ARB_vertex_shader)
        throw new RuntimeException("Pre OpenGL 2.0 without GL_ARB_vertex_shader support.");
      if (!GLContext.getCapabilities().GL_ARB_fragment_shader) {
        throw new RuntimeException("Pre OpenGL 2.0 without GL_ARB_vertex_shader support.");
      }
    }
    if (!this.pre_opengl20)
      this.vertex_shader = compile(35633, vertex_shader_source);
    else {
      this.vertex_shader = compilePre20(35633, vertex_shader_source);
    }
    if (this.vertex_shader == 0) System.out.println("Error Vertex Shader!");

    String fragment_shader_source = "";
    try
    {
      reader = new BufferedReader(new FileReader(f.getParent() + frag_path));
      while ((line = reader.readLine()) != null)
        fragment_shader_source = fragment_shader_source + line + "\n";
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    if (!this.pre_opengl20)
      this.fragment_shader = compile(35632, fragment_shader_source);
    else {
      this.fragment_shader = compilePre20(35632, fragment_shader_source);
    }
    if (this.fragment_shader == 0) System.out.println("Error Fragment Shader!");

    if (!this.pre_opengl20)
      link(path);
    else
      linkPre20(path);
  }

  private int compilePre20(int type, String shader_source)
  {
    int shader = ARBShaderObjects.glCreateShaderObjectARB(type);
    if (shader == 0) {
      System.out.println("Impossible to create shader object!");
      return 0;
    }

    ARBShaderObjects.glShaderSourceARB(shader, shader_source);
    ARBShaderObjects.glCompileShaderARB(shader);

    return shader;
  }

  private int compile(int type, String shader_source)
  {
    int shader = GL20.glCreateShader(type);
    if (shader == 0) {
      System.out.println("Impossible to create shader object!");
      return 0;
    }

    GL20.glShaderSource(shader, shader_source);
    GL20.glCompileShader(shader);

    return shader;
  }

  private void linkPre20(String source)
  {
    this.program = ARBShaderObjects.glCreateProgramObjectARB();

    ARBShaderObjects.glAttachObjectARB(this.program, this.vertex_shader);
    ARBShaderObjects.glAttachObjectARB(this.program, this.fragment_shader);

    ARBShaderObjects.glLinkProgramARB(this.program);

    IntBuffer result = BufferUtils.createIntBuffer(1);
    ARBShaderObjects.glGetObjectParameterARB(this.program, 35714, result);

    if (result.get(0) != 1) {
      throw new RuntimeException("Shader compile error! \n\nin: " + source + "\n\n" + ARBShaderObjects.glGetInfoLogARB(this.program, 5000));
    }

    System.out.println(ARBShaderObjects.glGetInfoLogARB(this.program, 5000));
  }

  private void link(String source)
  {
    this.program = GL20.glCreateProgram();

    GL20.glAttachShader(this.program, this.vertex_shader);
    GL20.glAttachShader(this.program, this.fragment_shader);

    GL20.glLinkProgram(this.program);

    IntBuffer result = BufferUtils.createIntBuffer(1);
    GL20.glGetProgram(this.program, 35714, result);

    if (result.get(0) != 1) {
      throw new RuntimeException("Shader compile error! \n\nin: " + source + "\n\n" + GL20.glGetProgramInfoLog(this.program, 5000));
    }

    System.out.println(GL20.glGetProgramInfoLog(this.program, 5000));
  }

  public void bind()
  {
    if (!this.pre_opengl20)
      GL20.glUseProgram(this.program);
    else
      ARBShaderObjects.glUseProgramObjectARB(this.program);
  }

  public void unbind()
  {
    if (!this.pre_opengl20)
      GL20.glUseProgram(0);
    else
      ARBShaderObjects.glUseProgramObjectARB(0);
  }

  public int initValue1i(String name)
  {
    int location = 0;
    if (!this.pre_opengl20)
      location = GL20.glGetUniformLocation(this.program, name);
    else {
      location = ARBShaderObjects.glGetUniformLocationARB(this.program, name);
    }

    if ((!this.disable_error_msg) && (location == -1)) System.out.println("Error binding shader int.");

    this.value_ids.add(Integer.valueOf(location));

    if (this.debug) System.out.println("Added: " + name + " at " + (this.value_ids.size() - 1) + " location: " + location);

    return this.value_ids.size() - 1;
  }

  public void setValue1i(int position_in_location_array, int value)
  {
    if (!this.pre_opengl20)
      GL20.glUniform1i(((Integer)this.value_ids.get(position_in_location_array)).intValue(), value);
    else
      ARBShaderObjects.glUniform1iARB(((Integer)this.value_ids.get(position_in_location_array)).intValue(), value);
  }

  public int initValue1f(String name)
  {
    int location = 0;
    if (!this.pre_opengl20)
      location = GL20.glGetUniformLocation(this.program, name);
    else {
      location = ARBShaderObjects.glGetUniformLocationARB(this.program, name);
    }

    if ((!this.disable_error_msg) && (location == -1)) System.out.println("Error binding shader float.");

    this.value_ids.add(Integer.valueOf(location));

    if (this.debug) System.out.println("Added: " + name + " at " + (this.value_ids.size() - 1) + " location: " + location);

    return this.value_ids.size() - 1;
  }

  public void setValue1f(int position_in_location_array, float value)
  {
    if (!this.pre_opengl20)
      GL20.glUniform1f(((Integer)this.value_ids.get(position_in_location_array)).intValue(), value);
    else
      ARBShaderObjects.glUniform1fARB(((Integer)this.value_ids.get(position_in_location_array)).intValue(), value);
  }

  public int initValueVec3f(String name)
  {
    int location = 0;
    if (!this.pre_opengl20)
      location = GL20.glGetUniformLocation(this.program, name);
    else {
      location = ARBShaderObjects.glGetUniformLocationARB(this.program, name);
    }

    if ((!this.disable_error_msg) && (location == -1)) System.out.println("Error binding shader vec3.");

    this.value_ids.add(Integer.valueOf(location));

    if (this.debug) System.out.println("Added: " + name + " at " + (this.value_ids.size() - 1) + " location: " + location);

    return this.value_ids.size() - 1;
  }

  public void setValueVec3f(int position_in_location_array, float x, float y, float z)
  {
    if (!this.pre_opengl20)
      GL20.glUniform3f(((Integer)this.value_ids.get(position_in_location_array)).intValue(), x, y, z);
    else
      ARBShaderObjects.glUniform3fARB(((Integer)this.value_ids.get(position_in_location_array)).intValue(), x, y, z);
  }

  public int initValueMat4f(String name)
  {
    int location = 0;
    if (!this.pre_opengl20)
      location = GL20.glGetUniformLocation(this.program, name);
    else {
      location = ARBShaderObjects.glGetUniformLocationARB(this.program, name);
    }

    if ((!this.disable_error_msg) && (location == -1)) System.out.println("Error binding shader matrix.");

    this.value_ids.add(Integer.valueOf(location));

    if (this.debug) System.out.println("Added: " + name + " at " + (this.value_ids.size() - 1) + " location: " + location);

    return this.value_ids.size() - 1;
  }

  public void setValueMat4f(int position_in_location_array, float[] value)
  {
    this.matrix.clear();
    for (int i = 0; i < 16; i++) {
      this.matrix.put(value[i]);
    }
    this.matrix.flip();

    if (!this.pre_opengl20)
      GL20.glUniformMatrix4(((Integer)this.value_ids.get(position_in_location_array)).intValue(), true, this.matrix);
    else
      ARBShaderObjects.glUniformMatrix4ARB(((Integer)this.value_ids.get(position_in_location_array)).intValue(), true, this.matrix);
  }
}